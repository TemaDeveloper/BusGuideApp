use axum::{
    body::Body,
    extract::{self, multipart::Multipart},
    http::{header, Response, StatusCode},
    response::IntoResponse,
    routing::{get, post},
    Extension, Json, Router,
};
use backend::schemas;
use bytes::BytesMut;
use dotenv::dotenv;
use sqlx::postgres::PgPoolOptions;
use std::{convert::Infallible, env};
use tokio::net::TcpListener;

mod constants;
mod user;
mod utils;

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();
    dotenv().expect("Could not read .env");
    let db_conn = PgPoolOptions::new()
        .max_connections(1)
        .connect(&env::var("DATABASE_URL").expect("DATABASE_URL env variable is not found"))
        .await
        .expect("Could not connect to DB, check url");

    let app = Router::new()
        .route("/organizator", post(create_organizator))
        .route("/organizator/:id", get(get_organizator))
        .route("/organizator/:id/avatar", get(get_organizator_avatar))
        .route("/user", post(user::create))
        .route("/user", get(user::get))
        .route("/user/:id/avatar", get(user::get_avatar))
        .layer(Extension(db_conn));

    let listener = TcpListener::bind("127.0.0.1:3000")
        .await
        .expect("Could not bind to localhost");

    if let Err(err) = axum::serve(listener, app).await {
        tracing::error!("{}", err.to_string());
    }
}

async fn get_organizator_avatar(
    extract::Path(id): extract::Path<i32>,
    Extension(db_conn): Extension<sqlx::PgPool>,
) -> impl IntoResponse {
    let img_bytes = sqlx::query!("SELECT avatar_img FROM organizators WHERE id = $1", id)
        .fetch_one(&db_conn)
        .await
        .unwrap()
        .avatar_img
        .unwrap_or_default();

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

async fn get_organizator(
    extract::Path(id): extract::Path<i32>,
    Extension(db_conn): Extension<sqlx::PgPool>,
) -> (StatusCode, Json<schemas::Organizator>) {
    let result = sqlx::query!(
        "SELECT  id, avatar_img, name, last_name, regular_number, email, whatsapp_number, tg_tag, viber_number  FROM organizators WHERE id = $1",
        id
    )
    .fetch_one(&db_conn)
    .await;

    match result {
        Ok(record) => {
            let organizator = schemas::Organizator {
                id: Some(record.id),
                name: record.name,
                last_name: record.last_name,
                regular_number: record.regular_number,
                email: record.email,
                whatsapp_number: record.whatsapp_number,
                tg_tag: record.tg_tag,
                viber_number: record.viber_number,
                avatar_img: Some(format!("127.0.0.1:3000/organizator/{id}/avatar")),
            };
            (StatusCode::OK, Json(organizator))
        }
        Err(_) => (
            StatusCode::INTERNAL_SERVER_ERROR,
            Json(schemas::Organizator::default()),
        ),
    }
}

async fn create_organizator(
    Extension(db_conn): Extension<sqlx::PgPool>,
    mut file: Multipart,
) -> (StatusCode, String) {
    let mut info = BytesMut::new();
    let mut avatar = BytesMut::new();

    while let Some(mut field) = file.next_field().await.unwrap() {
        if let Some(name) = field.name() {
            match name {
                "info" => {
                    while let Some(chunk) = field.chunk().await.unwrap() {
                        info.extend_from_slice(&chunk);
                    }
                }
                "avatar" => {
                    while let Some(chunk) = field.chunk().await.unwrap() {
                        if avatar.len() >= constants::AVATAR_SIZE_LIMIT {
                            return (
                                StatusCode::EXPECTATION_FAILED,
                                "Avatar size exceeds size limit".to_string(),
                            );
                        }
                        avatar.extend_from_slice(&chunk);
                    }
                }
                _ => {
                    return (
                        StatusCode::EXPECTATION_FAILED,
                        "Not correct name found in chunk".to_string(),
                    )
                }
            }
        } else {
            return (
                StatusCode::EXPECTATION_FAILED,
                "No field name is present".to_string(),
            );
        }
    }

    let info: schemas::Organizator = serde_json::from_slice(&info).expect("Unable to parse info");

    let res = sqlx::query!(
        r#"
        INSERT INTO organizators 
            (avatar_img, name, last_name, regular_number, email, whatsapp_number, tg_tag, viber_number)
        VALUES 
            ($1, $2, $3, $4, $5, $6, $7, $8)
        RETURNING id
        "#,
        &avatar as &[u8], /* just so dumb compiler understands what we want from it */
        info.name,
        info.last_name,
        info.regular_number,
        info.email,
        info.whatsapp_number,
        info.tg_tag,
        info.viber_number
    )
    .fetch_one(&db_conn)
    .await
    .unwrap();

    (StatusCode::CREATED, format!("User id: {}", res.id))
}
