use axum::{extract, response::IntoResponse};

pub async fn create(
    extract::Extension(db_conn): extract::Extension<sqlx::PgPool>,
) -> impl IntoResponse {

}
