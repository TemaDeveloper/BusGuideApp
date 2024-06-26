use std::convert::Infallible;

use axum::{
    body::Body,
    extract::{self, Multipart},
    response::{IntoResponse, Response},
    Extension, Json,
};
use backend::schemas;
use bytes::Bytes;
use hyper::{header, StatusCode};
use serde::{Deserialize, Serialize};
use sqlx::PgPool;

use crate::{constants, review, utils};
pub async fn create(
    Extension(db_conn): Extension<sqlx::PgPool>,
    mut req: Multipart,
) -> impl IntoResponse {
    let req = utils::multipart_to_map(&mut req).await;
    let null_bytes = Bytes::default();
    let body: schemas::Trip = serde_json::from_slice(req.get("info").unwrap()).unwrap();
    let trip_picture = req.get("trip_picture").unwrap_or(&null_bytes);
    let organizator_avatar = req.get("organizator_avatar").unwrap_or(&null_bytes);

    let pick_up_points = Some(body.pick_up_points);

    let trip_record = sqlx::query!(
        r#"
            INSERT INTO trips 
                (image, title, price, category, pick_up_points, plan)
            VALUES
                ($1, $2, $3, $4, $5, $6)
            RETURNING
                id
        "#,
        &trip_picture as &[u8],
        body.title,
        body.price,
        body.category,
        pick_up_points.as_deref(),
        body.plan,
    )
    .fetch_one(&db_conn)
    .await
    .unwrap();

    let _organizator_id = create_organizator(
        &db_conn,
        &body.organizator.unwrap_or_default(),
        &organizator_avatar,
        trip_record.id,
    )
    .await;

    for reiview in body.reviews {
        let _resp = review::create(
            extract::Path(trip_record.id),
            Extension(db_conn.clone()),
            Json(reiview),
        )
        .await;
    }

    (
        StatusCode::OK,
        format!("\"user_id\": \"{}\"", trip_record.id),
    )
}

async fn create_organizator(
    db_conn: &PgPool,
    organizator: &schemas::Organizator,
    avatar: &[u8],
    trip_id: i32,
) -> i32 {
    let res = sqlx::query!(
        r#"
        INSERT INTO organizators 
            (avatar_img, name, last_name, regular_number, email, whatsapp_number, tg_tag, viber_number, trip_id)
        VALUES 
            ($1, $2, $3, $4, $5, $6, $7, $8, $9)
        RETURNING id
        "#,
        &avatar as &[u8], /* just so dumb compiler understands what we want from it */
        organizator.name,
        organizator.last_name,
        organizator.regular_number,
        organizator.email,
        organizator.whatsapp_number,
        organizator.tg_tag,
        organizator.viber_number,
        trip_id
    )
    .fetch_one(db_conn)
    .await
    .unwrap();

    res.id
}

pub async fn get(
    Extension(db_conn): Extension<sqlx::PgPool>,
    extract::Path(trip_id): extract::Path<i32>,
) -> (StatusCode, Json<schemas::Trip>) {
    let trip = sqlx::query!("SELECT * FROM trips WHERE id = $1;", trip_id)
        .fetch_one(&db_conn)
        .await
        .unwrap();

    let organizator = sqlx::query!("SELECT * FROM organizators WHERE trip_id = $1;", trip_id)
        .fetch_one(&db_conn)
        .await;

    let reviews = sqlx::query!("SELECT * FROM reviews WHERE trip_id = $1;", trip_id)
        .fetch_all(&db_conn)
        .await
        .unwrap_or_default();

    (
        StatusCode::OK,
        Json(schemas::Trip {
            title: trip.title,
            category: trip.category,
            plan: trip.plan,
            price: trip.price,
            image: Some(format!("trip/{trip_id}/image")),
            pick_up_points: trip.pick_up_points,
            organizator: organizator.ok().map(|o| schemas::Organizator {
                email: o.email,
                name: o.name,
                last_name: o.last_name,
                id: Some(o.id),
                regular_number: o.regular_number,
                tg_tag: o.tg_tag,
                viber_number: o.viber_number,
                whatsapp_number: o.whatsapp_number,
                avatar_img: Some(format!("organizator/{}/avatar", o.id)),
            }),

            reviews: reviews
                .iter()
                .map(|r| schemas::Review {
                    description: r.description.clone(),
                    rating: r.rating,
                    user_id: r.user_id,
                })
                .collect(),
        }),
    )
}

pub async fn get_image(
    Extension(db_conn): Extension<sqlx::PgPool>,
    extract::Path(trip_id): extract::Path<i32>,
) -> impl IntoResponse {
    let img_bytes = sqlx::query!("SELECT image FROM trips WHERE id = $1", trip_id)
        .fetch_one(&db_conn)
        .await
        .unwrap()
        .image
        .unwrap_or_default();

    tracing::info!("Requesting image for Id({})", trip_id);

    if let Some(format) = utils::bytes_to_img_format(&img_bytes) {
        let chunks = img_bytes
            .chunks(constants::IMG_CHUNK_SIZE)
            .map(Vec::from) /* copy data */
            .map(Ok::<_, Infallible>) /* transform into Result<Vec<u8>, Infallible> */
            .collect::<Vec<_>>(); /* wierd lifetime issues */

        Response::builder()
            .status(StatusCode::OK)
            .header(header::CONTENT_TYPE, format)
            .body(Body::from_stream(tokio_stream::iter(chunks)))
            .unwrap()
    } else {
        Response::builder()
            .status(StatusCode::NOT_FOUND)
            .body(Body::from(img_bytes))
            .unwrap()
    }
}

#[derive(Deserialize, Serialize, Default)]
struct GetAllResponse {
    trips_ids: Vec<i32>,
}

pub async fn get_all(Extension(db_conn): Extension<sqlx::PgPool>) -> impl IntoResponse {
    let q = sqlx::query!("SELECT id FROM trips;")
        .fetch_all(&db_conn)
        .await;

    if let Ok(q) = q {
        (
            StatusCode::OK,
            Json(GetAllResponse {
                trips_ids: q.iter().map(|q| q.id).collect(),
            }),
        )
    } else {
        (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(GetAllResponse::default()),
        )
    }
}

pub async fn delete(extract::Path(trip_id): extract::Path<i32>, Extension(db_conn): Extension<sqlx::PgPool>) -> impl IntoResponse {
    if let Err(e) = sqlx::query!(
        "DELETE FROM trips WHERE id = $1", trip_id)
        .execute(&db_conn)
        .await
    {
        tracing::warn!("Error deleting Trip{trip_id}, Error: {}", e);
        return StatusCode::NOT_FOUND;
    }

    if let Err(e) = sqlx::query!(
        "DELETE FROM reservations WHERE trip_id = $1", trip_id)
        .execute(&db_conn)
        .await
    {
        tracing::warn!("Error deleting Trip{trip_id}, Reservation Issue(This should never happen), Error: {}", e);
        return StatusCode::NOT_FOUND;
    }

    if let Err(e) = sqlx::query!(
        "DELETE FROM organizators WHERE trip_id = $1", trip_id)
        .execute(&db_conn)
        .await
    {
        tracing::warn!("Error deleting Trip{trip_id}, Organizator Issue(This should never happen), Error: {}", e);
        return StatusCode::NOT_FOUND;
    }

    if let Err(e) = sqlx::query!(
        "DELETE FROM reviews WHERE trip_id = $1", trip_id)
        .execute(&db_conn)
        .await
    {
        tracing::warn!("Error deleting Trip{trip_id}, Review Issue(This should never happen), Error: {}", e);
        return StatusCode::NOT_FOUND;
    }

    StatusCode::OK
}
