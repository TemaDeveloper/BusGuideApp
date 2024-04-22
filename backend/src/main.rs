use anyhow::Context;
use axum::{
    extract::{multipart::Multipart, DefaultBodyLimit},
    http::StatusCode,
    routing::{get, post},
    Json, Router,
};
use backend::schemas;
use bytes::BytesMut;
use dotenv::dotenv;
use sqlx::postgres::PgPoolOptions;
use static_cell::StaticCell;
use std::{env, fs};
use tokio::net::TcpListener;
use tower_http::limit::RequestBodyLimitLayer;

static DB_CONN: StaticCell<sqlx::PgPool> = StaticCell::new();

async fn init_static() {
    dotenv().expect("Could not read .env");
    let pg_pool = PgPoolOptions::new()
        .max_connections(1)
        .connect(&env::var("DATABASE_URL").expect("DATABASE_URL env variable is not found"))
        .await
        .expect("Could not connect to DB, check url");

    DB_CONN.init(pg_pool);
}

#[tokio::main]
async fn main() {
    tracing_subscriber::fmt::init();
    init_static().await; /* should always be awaited before anything */

    let app = Router::new()
        .route("/", get(root))
        .route("/users", post(create_organizator));

    let listener = TcpListener::bind("127.0.0.1:3000")
        .await
        .expect("Could not bind to localhost");

    if let Err(err) = axum::serve(listener, app).await {
        tracing::error!("{}", err.to_string());
    }
}

async fn root() -> &'static str {
    "Hello, World!"
}

fn bytes_to_file(path: &str, content: &[u8]) -> anyhow::Result<()> {
    fs::write(path, content)
        .with_context(|| format!("Failed to write to file `{}`", path))?;
    Ok(())
}

async fn create_organizator(
    // Json(payload): Json<schemas::Organizator>,
    mut file: Multipart,
) -> (StatusCode, String) {
    let mut info = BytesMut::new();
    let mut avatar = BytesMut::new();

    while let Some(mut field) = file.next_field().await.unwrap() {
        if let Some(name) = field.name() {
            match name {
                "info" => info.extend_from_slice(&field.chunk().await.unwrap().unwrap()),
                "avatar" => avatar.extend_from_slice(&field.chunk().await.unwrap().unwrap()),
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
    tracing::info!("Got info: {info:?}");
    bytes_to_file("./main.rs.server", &avatar).unwrap();

    (
        StatusCode::CREATED,
        "Sucsefully created An Organizator".to_string(),
    )
}
