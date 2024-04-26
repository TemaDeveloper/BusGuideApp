use std::collections::HashMap;

use axum::extract::Multipart;
use bytes::{Bytes, BytesMut};

/// If you don't understand where I got those magic numbers from
/// I don't understand either, I asked ChatGpt and prayed they were right
pub fn bytes_to_img_format(bytes: &[u8]) -> Option<&'static str> {
    match bytes {
        [0x89, b'P', b'N', b'G', 0x0D, 0x0A, 0x1A, 0x0A, ..] => Some("image/png"),
        [0xFF, 0xD8, 0xFF, ..] => Some("image/jpeg"),
        [b'R', b'I', b'F', b'F', _, _, _, _, b'W', b'E', b'B', b'P', ..] => Some("image/webp"),
        _ => None,
    }
}

pub async fn multipart_to_map(mp: &mut Multipart) -> HashMap<String, Bytes> 
{
    let mut map = HashMap::new();

    while let Some(field) = mp.next_field().await.unwrap() {
        map.insert(
            field.name()
                .unwrap()
                .to_string(), 
            field.bytes()
                .await
                .unwrap()
        );
    }

    map
}
