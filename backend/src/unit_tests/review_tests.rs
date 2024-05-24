#[cfg(test)]
mod tests {
    use super::*;
    use axum::body::Body;
    use axum::extract::Extension;
    use axum::http::{Request, StatusCode};
    use serde_json::json;
    use sqlx::{Executor, Pool, Postgres};
    use tower::ServiceExt; // for `app.oneshot()`

    async fn setup_db() -> Pool<Postgres> {
        let pool = Pool::<Postgres>::connect("postgres://user:password@localhost/test_db").await.unwrap();
        
        // Set up the database schema for testing
        pool.execute(
            r#"
            CREATE TABLE IF NOT EXISTS reviews (
                id SERIAL PRIMARY KEY,
                user_id INTEGER NOT NULL,
                trip_id INTEGER NOT NULL,
                rating INTEGER NOT NULL,
                comment TEXT,
                created_at TIMESTAMP DEFAULT NOW()
            )
            "#
        ).await.unwrap();

        pool.execute(
            r#"
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255),
                email VARCHAR(255),
                password VARCHAR(255),
                is_admin BOOLEAN
            )
            "#
        ).await.unwrap();

        pool.execute(
            r#"
            CREATE TABLE IF NOT EXISTS trips (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255),
                category VARCHAR(255),
                description TEXT,
                price INTEGER,
                duration INTEGER
            )
            "#
        ).await.unwrap();

        // Insert a test user and a test trip
        pool.execute(
            "INSERT INTO users (name, email, password, is_admin) VALUES ('testuser', 'test@example.com', 'password', false)"
        ).await.unwrap();

        pool.execute(
            "INSERT INTO trips (name, category, description, price, duration) VALUES ('Trip 1', 'Category 1', 'Description 1', 100, 1)"
        ).await.unwrap();

        pool
    }

    #[tokio::test]
    async fn test_create_review_success() {
        let db_conn = setup_db().await;

        let review_data = json!({
            "user_id": 1,
            "trip_id": 1,
            "rating": 5,
            "comment": "Great trip!"
        });

        let request = Request::builder()
            .method("POST")
            .uri("/reviews")
            .header("content-type", "application/json")
            .body(Body::from(review_data.to_string()))
            .unwrap();

        let response = create_review(Extension(db_conn.clone()), request.into()).await.into_response();

        assert_eq!(response.status(), StatusCode::CREATED);
    }

    #[tokio::test]
    async fn test_create_review_invalid_data() {
        let db_conn = setup_db().await;

        let review_data = json!({
            "user_id": 1,
            "trip_id": 1,
            // Missing rating field
            "comment": "Great trip!"
        });

        let request = Request::builder()
            .method("POST")
            .uri("/reviews")
            .header("content-type", "application/json")
            .body(Body::from(review_data.to_string()))
            .unwrap();

        let response = create_review(Extension(db_conn.clone()), request.into()).await.into_response();

        assert_eq!(response.status(), StatusCode::BAD_REQUEST);
    }

    #[tokio::test]
    async fn test_get_review_by_id_success() {
        let db_conn = setup_db().await;

        // Insert a test review
        pool.execute(
            "INSERT INTO reviews (user_id, trip_id, rating, comment) VALUES (1, 1, 5, 'Great trip!')"
        ).await.unwrap();

        let request = Request::builder()
            .method("GET")
            .uri("/reviews/1")
            .body(Body::empty())
            .unwrap();

        let response = get_review_by_id(Extension(db_conn.clone()), extract::Path(1)).await.into_response();

        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_get_review_by_id_not_found() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("GET")
            .uri("/reviews/999")
            .body(Body::empty())
            .unwrap();

        let response = get_review_by_id(Extension(db_conn.clone()), extract::Path(999)).await.into_response();

        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }

    #[tokio::test]
    async fn test_update_review_success() {
        let db_conn = setup_db().await;

        // Insert a test review
        pool.execute(
            "INSERT INTO reviews (user_id, trip_id, rating, comment) VALUES (1, 1, 5, 'Great trip!')"
        ).await.unwrap();

        let updated_review_data = json!({
            "rating": 4,
            "comment": "Good trip!"
        });

        let request = Request::builder()
            .method("PUT")
            .uri("/reviews/1")
            .header("content-type", "application/json")
            .body(Body::from(updated_review_data.to_string()))
            .unwrap();

        let response = update_review(Extension(db_conn.clone()), extract::Path(1), request.into()).await.into_response();

        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_update_review_not_found() {
        let db_conn = setup_db().await;

        let updated_review_data = json!({
            "rating": 4,
            "comment": "Good trip!"
        });

        let request = Request::builder()
            .method("PUT")
            .uri("/reviews/999")
            .header("content-type", "application/json")
            .body(Body::from(updated_review_data.to_string()))
            .unwrap();

        let response = update_review(Extension(db_conn.clone()), extract::Path(999), request.into()).await.into_response();

        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }

    #[tokio::test]
    async fn test_delete_review_success() {
        let db_conn = setup_db().await;

        // Insert a test review
        pool.execute(
            "INSERT INTO reviews (user_id, trip_id, rating, comment) VALUES (1, 1, 5, 'Great trip!')"
        ).await.unwrap();

        let request = Request::builder()
            .method("DELETE")
            .uri("/reviews/1")
            .body(Body::empty())
            .unwrap();

        let response = delete_review(Extension(db_conn.clone()), extract::Path(1)).await.into_response();

        assert_eq!(response.status(), StatusCode::NO_CONTENT);
    }

    #[tokio::test]
    async fn test_delete_review_not_found() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("DELETE")
            .uri("/reviews/999")
            .body(Body::empty())
            .unwrap();

        let response = delete_review(Extension(db_conn.clone()), extract::Path(999)).await.into_response();

        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }

    #[tokio::test]
    async fn test_get_reviews_by_trip_success() {
        let db_conn = setup_db().await;

        // Insert a test review
        pool.execute(
            "INSERT INTO reviews (user_id, trip_id, rating, comment) VALUES (1, 1, 5, 'Great trip!')"
        ).await.unwrap();

        let request = Request::builder()
            .method("GET")
            .uri("/trips/1/reviews")
            .body(Body::empty())
            .unwrap();

        let response = get_reviews_by_trip(Extension(db_conn.clone()), extract::Path(1)).await.into_response();

        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_get_reviews_by_trip_not_found() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("GET")
            .uri("/trips/999/reviews")
            .body(Body::empty())
            .unwrap();

        let response = get_reviews_by_trip(Extension(db_conn.clone()), extract::Path(999)).await.into_response();

        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }
}
