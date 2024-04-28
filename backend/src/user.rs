use std::convert::Infallible;

use crate::{constants, utils};
use axum::{
    body::Body,
    extract::{self, multipart::Multipart},
    http::StatusCode,
    response::{IntoResponse, Response},
    Extension, Json,
};
use backend::schemas;
use bytes::Bytes;
use hyper::header;
use serde::{Deserialize, Serialize};

pub async fn register(
    Extension(db_conn): Extension<sqlx::PgPool>,
    mut req: Multipart,
) -> impl IntoResponse {
    let req = utils::multipart_to_map(&mut req).await;
    let avatar = req
        .get("avatar")
        .map_or(constants::DEFUALT_AVATAR_BYTES.to_vec(), |a| {
            if utils::bytes_to_img_format(a).is_some() {
                a.to_vec()
            } else {
                constants::DEFUALT_AVATAR_BYTES.to_vec()
            }
        });
    let req: schemas::User = serde_json::from_slice(&req.get("info").unwrap()).unwrap();

    if avatar.len() >= constants::AVATAR_SIZE_LIMIT {
        return (
            StatusCode::EXPECTATION_FAILED,
            "Invalid image or exceeded size".into(),
        );
    }

    let res = sqlx::query!(
        r#"
        INSERT INTO users 
            (avatar, name, email, password, is_admin)
        VALUES 
            ($1, $2, $3, $4, $5)
        RETURNING id
        "#,
        &avatar as &[u8], /* just so dumb compiler understands what we want from it */
        req.name,
        req.email,
        req.password,
        req.is_admin,
    )
    .fetch_one(&db_conn)
    .await
    .unwrap();

    (StatusCode::CREATED, format!("{}", res.id))
}

/// Atleast one of email or name should be Some
#[derive(Debug, Deserialize)]
pub struct GetReqBody {
    pub email: Option<String>,
    pub name: Option<String>,
    pub password: String,
}

#[derive(Debug, Serialize)]
pub enum GetResponse {
    User(schemas::User),
    Error(String),
}

/// To keep naming consistent, this one actually
/// is actually used to authenticate a user
pub async fn auth(
    Extension(db_conn): Extension<sqlx::PgPool>,
    Json(user_info): Json<GetReqBody>,
) -> (StatusCode, Json<GetResponse>) {
    let q = sqlx::query!(
        "SELECT * FROM users WHERE (name = $1 OR email = $2) and (password = $3);",
        user_info.name.unwrap_or_default(),
        user_info.email.unwrap_or_default(),
        user_info.password
    )
    .fetch_one(&db_conn)
    .await;

    if let Ok(user) = q {
        let user = schemas::User {
            id: user.id,
            name: user.name,
            email: user.email,
            is_admin: user.is_admin,
            password: None, // do not send it back
            avatar: Some(format!("user/{}/avatar", user.id)),
        };
        (StatusCode::OK, Json(GetResponse::User(user)))
    } else {
        (
            StatusCode::NOT_FOUND,
            Json(GetResponse::Error("Invalid password Or Email/Name".into())),
        )
    }
}

pub async fn get_avatar(
    Extension(db_conn): Extension<sqlx::PgPool>,
    extract::Path(id): extract::Path<i32>,
) -> Response {
    let avatar_bytes = sqlx::query!("SELECT avatar FROM users WHERE id = $1", id)
        .fetch_one(&db_conn)
        .await
        .unwrap()
        .avatar
        .unwrap_or(constants::DEFUALT_AVATAR_BYTES.to_vec());

    if let Some(format) = utils::bytes_to_img_format(&avatar_bytes) {
        let chunks = avatar_bytes
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
            .body(Body::from(avatar_bytes))
            .unwrap()
    }
}

pub async fn update_avatar(
    Extension(db_conn): Extension<sqlx::PgPool>,
    extract::Path(id): extract::Path<i32>,
    mut file: Multipart,
) -> impl IntoResponse {
    let null_bytes = Bytes::default();
    let req = utils::multipart_to_map(&mut file).await;
    let img_bytes = req.get("avatar").unwrap_or(&null_bytes);

    let q = sqlx::query!(
        "UPDATE users SET avatar = $1 WHERE id = $2",
        &img_bytes as &[u8],
        id
    )
    .fetch_one(&db_conn)
    .await;

    if q.is_ok() {
        StatusCode::OK
    } else {
        StatusCode::NOT_FOUND
    }
}

pub async fn get(
    Extension(db_conn): Extension<sqlx::PgPool>,
    extract::Path(user_id): extract::Path<i32>,
) -> impl IntoResponse {
    let q = sqlx::query!("SELECT * FROM users WHERE id = $1;", user_id)
        .fetch_one(&db_conn)
        .await;

    if let Ok(user) = q {
        let user = schemas::User {
            id: user.id,
            name: user.name,
            email: user.email,
            is_admin: user.is_admin,
            password: None, // do not send it back
            //TODO: remove hard coded value
            avatar: Some(format!("http://127.0.0.1:3000/user/{}/avatar", user.id)),
        };
        (StatusCode::OK, Json(GetResponse::User(user)))
    } else {
        (
            StatusCode::NOT_FOUND,
            Json(GetResponse::Error("Invalid password Id".into())),
        )
    }
}

#[derive(Deserialize, Serialize)]
pub struct ReserveReqBody {
    pub user_id: i32,
    pub trip_id: i32,
}

pub async fn reserve(
    Extension(db_conn): Extension<sqlx::PgPool>,
    Json(req): Json<ReserveReqBody>,
) -> impl IntoResponse {
    let res = sqlx::query!(
        "INSERT INTO reservations (user_id, trip_id) VALUES ($1, $2)",
        req.user_id,
        req.trip_id
    )
    .fetch_one(&db_conn)
    .await;

    match res {
        Ok(_) => (StatusCode::OK, ""),
        Err(_) => (StatusCode::INTERNAL_SERVER_ERROR, ""),
    }
}

#[derive(Deserialize, Serialize, Default)]
pub struct ReservationsResponse {
    trip_ids: Vec<i32>,
}

pub async fn get_reservations(
    extract::Path(user_id): extract::Path<i32>,
    Extension(db_conn): Extension<sqlx::PgPool>,
) -> (StatusCode, Json<ReservationsResponse>) {
    let q = sqlx::query!(
        "SELECT (trip_id) FROM reservations WHERE user_id = $1",
        user_id
    )
    .fetch_all(&db_conn)
    .await;

    match q {
        Ok(res) => (
            StatusCode::OK,
            Json(ReservationsResponse {
                trip_ids: res.iter().map(|r| r.trip_id).collect(),
            }),
        ),
        Err(_) => (StatusCode::NOT_FOUND, Json::default()),
    }
}
