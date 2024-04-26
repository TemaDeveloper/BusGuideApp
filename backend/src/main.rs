use axum::{
    body::Body,
    extract,
    http::{header, Response, StatusCode},
    response::IntoResponse,
    routing::{get, post},
    Extension, Router,
};
use dotenv::dotenv;
use sqlx::postgres::PgPoolOptions;
use std::{convert::Infallible, env};
use tokio::net::TcpListener;

mod constants;
mod user;
mod trip;
mod review;
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
        .route("/organizator/:id/avatar", get(get_organizator_avatar))

        .route("/register", post(user::register))
        .route("/user-auth", get(user::auth))
        .route("/user/:id/avatar", get(user::get_avatar))

        .route("/trip", post(trip::create))
        .route("/trip/:id", get(trip::get))
        .route("/trip/:id/image", get(trip::get_image))
        .route("/trip/:id/review", post(review::create))
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
