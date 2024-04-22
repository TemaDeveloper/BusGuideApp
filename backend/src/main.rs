use std::env;
use axum::{
    http::StatusCode,
    routing::{get, post},
    Json, Router,
};
use backend::schemas;
use dotenv::dotenv;
use sqlx::postgres::PgPoolOptions;
use static_cell::StaticCell;
use tokio::net::TcpListener;

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

async fn create_organizator(Json(payload): Json<schemas::Organizator>) -> (StatusCode, String) {
    dbg!(&payload);

    (
        StatusCode::CREATED,
        "Sucsefully created An Organizator".to_string(),
    )
}
