use axum::{extract, response::IntoResponse, Extension, Json};
use backend::schemas;
use hyper::StatusCode;
use sqlx::PgPool;

pub async fn create(
    extract::Path(trip_id): extract::Path<i32>,
    Extension(db_conn): Extension<PgPool>,
    Json(review): Json<schemas::Review>,
) -> impl IntoResponse {
    let record = sqlx::query!(
        r#"
        INSERT INTO reviews 
            (rating, description, trip_id, user_id)
        VALUES 
            ($1, $2, $3, $4)
        RETURNING id
        "#,
        review.rating,
        review.description,
        trip_id,
        review.user_id,
    )
    .fetch_one(&db_conn)
    .await;

    if let Ok(record) = record {
        (StatusCode::OK, format!("Id of review: {}", record.id))
    } else {
        (StatusCode::INTERNAL_SERVER_ERROR, "".to_string())
    }
}
