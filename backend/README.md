### Sending test requests
```
    curl -X POST -H "Content-Type: application/json" -d @test_req_body.json http://127.0.0.1:3000/...
```

### Running db
```
docker-compose up
```

### Running Backend
```
cargo run
```

### Running Migrations
```
#install sqlx-cli
cargo install sqlx-cli --no-default-features --features native-tls,postgres
#run
sqlx migrate run 
```
