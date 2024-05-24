#[cfg(test)]
mod tests {
    use super::*;
    use axum::body::Body;
    use axum::http::{Request, StatusCode};
    use axum::extract::Extension;
    use sqlx::{Pool, Postgres, Executor};
    use tempfile::NamedTempFile;
    use tokio::sync::Mutex;
    use tower::ServiceExt;

    async fn setup_db() -> Pool<Postgres> {
        let pool = Pool::<Postgres>::connect("postgres://user:password@localhost/test_db").await.unwrap();
        
        // Set up the database schema for testing
        pool.execute(
            r#"
            CREATE TABLE users (
                id SERIAL PRIMARY KEY,
                avatar BYTEA,
                name VARCHAR(255),
                email VARCHAR(255),
                password VARCHAR(255),
                is_admin BOOLEAN
            )
            "#
        ).await.unwrap();

        pool
    }

    #[tokio::test]
    async fn test_register() {
        let db_conn = setup_db().await;

        // Simulate the multipart form data
        let multipart_data = format!(
            "--boundary\r\n\
            Content-Disposition: form-data; name=\"avatar\"; filename=\"avatar.png\"\r\n\
            Content-Type: image/png\r\n\r\n\
            {avatar}\r\n\
            --boundary\r\n\
            Content-Disposition: form-data; name=\"info\"\r\n\r\n\
            {info}\r\n\
            --boundary--\r\n",
            avatar = base64::encode(constants::DEFAULT_AVATAR_BYTES),
            info = serde_json::to_string(&schemas::User {
                name: "Test User".into(),
                email: "test@example.com".into(),
                password: "password".into(),
                is_admin: false,
            }).unwrap(),
        );

        let request = Request::builder()
            .method("POST")
            .uri("/register")
            .header("content-type", "multipart/form-data; boundary=boundary")
            .body(Body::from(multipart_data))
            .unwrap();

        let response = register(Extension(db_conn.clone()), request.into()).await;

        // Assert the status code
        assert_eq!(response.status(), StatusCode::CREATED);

        // Verify the user is added to the database
        let user = sqlx::query!("SELECT * FROM users WHERE email = 'test@example.com'")
            .fetch_one(&db_conn)
            .await
            .unwrap();
        
        assert_eq!(user.name, "Test User");
        assert_eq!(user.email, "test@example.com");
        assert_eq!(user.is_admin, false);
    }

    async fn setup_db() -> Pool<Postgres> {
        let pool = Pool::<Postgres>::connect("postgres://user:password@localhost/test_db").await.unwrap();
        
        // Set up the database schema for testing
        pool.execute(
            r#"
            CREATE TABLE users (
                id SERIAL PRIMARY KEY,
                name VARCHAR(255),
                email VARCHAR(255),
                password VARCHAR(255),
                is_admin BOOLEAN,
                avatar BYTEA
            )
            "#
        ).await.unwrap();

        // Insert a test user
        pool.execute(
            "INSERT INTO users (name, email, password, is_admin) VALUES ('testuser', 'test@example.com', 'password', false)"
        ).await.unwrap();

        pool
    }

    #[tokio::test]
    async fn test_auth_success() {
        let db_conn = setup_db().await;

        let user_info = json!({
            "name": "testuser",
            "email": "test@example.com",
            "password": "password"
        });

        let request = Request::builder()
            .method("POST")
            .uri("/auth")
            .header("content-type", "application/json")
            .body(Body::from(user_info.to_string()))
            .unwrap();

        let response = auth(Extension(db_conn.clone()), request.into()).await;

        assert_eq!(response.0, StatusCode::OK);
    }

    #[tokio::test]
    async fn test_auth_failure() {
        let db_conn = setup_db().await;

        let user_info = json!({
            "name": "nonexistent",
            "email": "nonexistent@example.com",
            "password": "wrongpassword"
        });

        let request = Request::builder()
            .method("POST")
            .uri("/auth")
            .header("content-type", "application/json")
            .body(Body::from(user_info.to_string()))
            .unwrap();

        let response = auth(Extension(db_conn.clone()), request.into()).await;

        assert_eq!(response.0, StatusCode::NOT_FOUND);
    }

    #[tokio::test]
    async fn test_get_avatar_success() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("GET")
            .uri("/user/1/avatar")
            .body(Body::empty())
            .unwrap();

        let response = get_avatar(Extension(db_conn.clone()), extract::Path(1)).await;

        assert_eq!(response.status(), StatusCode::OK);
}

    #[tokio::test]
    async fn test_get_avatar_not_found() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("GET")
            .uri("/user/999/avatar")
            .body(Body::empty())
            .unwrap();

        let response = get_avatar(Extension(db_conn.clone()), extract::Path(999)).await;

        assert_eq!(response.status(), StatusCode::NOT_FOUND);
}

    #[tokio::test]
    async fn test_update_avatar_success() {
        let db_conn = setup_db().await;

        let multipart_data = "--boundary\r\n\
            Content-Disposition: form-data; name=\"avatar\"; filename=\"avatar.png\"\r\n\
            Content-Type: image/png\r\n\r\n\
            AVATARBYTES\r\n\
            --boundary--\r\n";

        let request = Request::builder()
            .method("POST")
            .uri("/user/1/avatar")
            .header("content-type", "multipart/form-data; boundary=boundary")
            .body(Body::from(multipart_data))
            .unwrap();

        let response = update_avatar(Extension(db_conn.clone()), extract::Path(1), request.into()).await;

        assert_eq!(response.into_response().status(), StatusCode::OK);
}

    #[tokio::test]
    async fn test_update_avatar_failure() {
        let db_conn = setup_db().await;

        let multipart_data = "--boundary\r\n\
            Content-Disposition: form-data; name=\"avatar\"; filename=\"avatar.png\"\r\n\
            Content-Type: image/png\r\n\r\n\
            AVATARBYTES\r\n\
            --boundary--\r\n";

        let request = Request::builder()
            .method("POST")
            .uri("/user/999/avatar")
            .header("content-type", "multipart/form-data; boundary=boundary")
            .body(Body::from(multipart_data))
            .unwrap();

        let response = update_avatar(Extension(db_conn.clone()), extract::Path(999), request.into()).await;

        assert_eq!(response.into_response().status(), StatusCode::NOT_FOUND);
}

    #[tokio::test]
    async fn test_get_user_success() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("GET")
            .uri("/user/1")
            .body(Body::empty())
            .unwrap();

        let response = get(Extension(db_conn.clone()), extract::Path(1)).await;

        assert_eq!(response.into_response().status(), StatusCode::OK);
}

    #[tokio::test]
    async fn test_get_user_not_found() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("GET")
            .uri("/user/999")
            .body(Body::empty())
            .unwrap();

        let response = get(Extension(db_conn.clone()), extract::Path(999)).await;

        assert_eq!(response.into_response().status(), StatusCode::NOT_FOUND);
}

#[tokio::test]
async fn test_reserve_success() {
    let db_conn = setup_db().await;

    let reservation = json!({
        "user_id": 1,
        "trip_id": 1,
        "price": 100,
        "num_people": 2,
        "date": "2024-05-24"
    });

    let request = Request::builder()
        .method("POST")
        .uri("/reserve")
        .header("content-type", "application/json")
        .body(Body::from(reservation.to_string()))
        .unwrap();

    let response = reserve(Extension(db_conn.clone()), request.into()).await;

    assert_eq!(response.into_response().status(), StatusCode::OK);
}

    #[tokio::test]
    async fn test_reserve_failure() {
        let db_conn = setup_db().await;

        let reservation = json!({
            "user_id": 999,
            "trip_id": 1,
            "price": 100,
            "num_people": 2,
            "date": "2024-05-24"
        });

        let request = Request::builder()
            .method("POST")
            .uri("/reserve")
            .header("content-type", "application/json")
            .body(Body::from(reservation.to_string()))
            .unwrap();

        let response = reserve(Extension(db_conn.clone()), request.into()).await;

        assert_eq!(response.into_response().status(), StatusCode::INTERNAL_SERVER_ERROR);
}

    #[tokio::test]
    async fn test_get_reservations_success() {
        let db_conn = setup_db().await;

        let request = Request::builder()
            .method("GET")
            .uri("/user/1/reservations")
            .body(Body::empty())
            .unwrap();

        let response = get_reservations(extract::Path(1), Extension(db_conn.clone())).await;

        assert_eq!(response.0, StatusCode::OK);
}

    #[tokio::test]
    async fn test_get_reservations_not_found() {
        let db_conn = setup_db().await;
    
        let request = Request::builder()
            .method("GET")
            .uri("/user/999/reservations")
            .body(Body::empty())
            .unwrap();
    
        let response = get_reservations(extract::Path(999), Extension(db_conn.clone())).await;
    
        assert_eq!(response.0, StatusCode::NOT_FOUND);
    }

}
