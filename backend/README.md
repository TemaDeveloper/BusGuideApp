### Sending test requests
```
curl -X POST -F "info=@test_req_body.json;type=application/json" -F "avatar=@./src/main.rs" http://127.0.0.1:3000/users
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

### Print db
```
psql -U admin -h localhost -p 10100 -d mydb -W -c "SELECT * FROM organizators;" -P "border=1"
```
