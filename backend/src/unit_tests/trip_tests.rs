use axum::{
    body::Body,
    extract::Extension,
    http::{Request, StatusCode},
    response::IntoResponse,
};
use serde_json::json;
use sqlx::{Executor, Pool, Postgres};
use tower::ServiceExt; // for `app.oneshot()`

// Import the functions from trip.rs
use crate::trip::{create_trip, delete_trip, get_trip_by_id, get_trips, update_trip};

#[cfg(test)]
mod trip_tests {
    use super::*;
    use std::net::TcpListener;

    async fn setup_db() -> Pool<Postgres> {
        let pool = Pool::<Postgres>::connect("postgres://user:password@localhost/test_db").await.unwrap();
        
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

        pool.execute(
            "INSERT INTO trips (name, category, description, price, duration) VALUES 
            ('Trip 1', 'Category 1', 'Description 1', 100, 1),
            ('Trip 2', 'Category 2', 'Description 2', 200, 2)"
        ).await.unwrap();

        pool
    }

    #[tokio::test]
    async fn test_get_trips_success() {
        let db_conn = setup_db().await;

        let response = get_trips(Extension(db_conn)).await.into_response();
        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_get_trips_empty() {
        let db_conn = Pool::<Postgres>::connect("postgres://user:password@localhost/empty_test_db").await.unwrap();
        
        db_conn.execute(
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

        let response = get_trips(Extension(db_conn)).await.into_response();
        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }

    #[tokio::test]
    async fn test_create_trip_success() {
        let db_conn = setup_db().await;

        let trip_data = json!({
            "name": "New Trip",
            "category": "New Category",
            "description": "New Description",
            "price": 300,
            "duration": 3
        });

        let request = Request::builder()
            .method("POST")
            .uri("/trips")
            .header("content-type", "application/json")
            .body(Body::from(trip_data.to_string()))
            .unwrap();

        let response = create_trip(Extension(db_conn), request.into()).await.into_response();
        assert_eq!(response.status(), StatusCode::CREATED);
    }

    #[tokio::test]
    async fn test_create_trip_invalid_data() {
        let db_conn = setup_db().await;

        let trip_data = json!({
            "name": "Invalid Trip",
        });

        let request = Request::builder()
            .method("POST")
            .uri("/trips")
            .header("content-type", "application/json")
            .body(Body::from(trip_data.to_string()))
            .unwrap();

        let response = create_trip(Extension(db_conn), request.into()).await.into_response();
        assert_eq!(response.status(), StatusCode::BAD_REQUEST);
    }

    #[tokio::test]
    async fn test_get_trip_by_id_success() {
        let db_conn = setup_db().await;

        let response = get_trip_by_id(Extension(db_conn), extract::Path(1)).await.into_response();
        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_get_trip_by_id_not_found() {
        let db_conn = setup_db().await;

        let response = get_trip_by_id(Extension(db_conn), extract::Path(999)).await.into_response();
        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }

    #[tokio::test]
    async fn test_update_trip_success() {
        let db_conn = setup_db().await;

        let trip_data = json!({
            "name": "Updated Trip",
            "category": "Updated Category",
            "description": "Updated Description",
            "price": 400,
            "duration": 4
        });

        let request = Request::builder()
            .method("PUT")
            .uri("/trips/1")
            .header("content-type", "application/json")
            .body(Body::from(trip_data.to_string()))
            .unwrap();

        let response = update_trip(Extension(db_conn), extract::Path(1), request.into()).await.into_response();
        assert_eq!(response.status(), StatusCode::OK);
    }

    #[tokio::test]
    async fn test_update_trip_not_found() {
        let db_conn = setup_db().await;

        let trip_data = json!({
            "name": "Nonexistent Trip",
            "category": "Nonexistent Category",
            "description": "Nonexistent Description",
            "price": 500,
            "duration": 5
        });

        let request = Request::builder()
            .method("PUT")
            .uri("/trips/999")
            .header("content-type", "application/json")
            .body(Body::from(trip_data.to_string()))
            .unwrap();

        let response = update_trip(Extension(db_conn), extract::Path(999), request.into()).await.into_response();
        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }

    #[tokio::test]
    async fn test_delete_trip_success() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("DELETE")
            .uri("/trips/1")
            .body(Body::empty())
            .unwrap();

        let response = delete_trip(Extension(db_conn), extract::Path(1)).await.into_response();
        assert_eq!(response.status(), StatusCode::NO_CONTENT);
    }

    #[tokio::test]
    async fn test_delete_trip_not_found() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("DELETE")
            .uri("/trips/999")
            .body(Body::empty())
            .unwrap();

        let response = delete_trip(Extension(db_conn), extract::Path(999)).await.into_response();
        assert_eq!(response.status(), StatusCode::NOT_FOUND);
    }
}
