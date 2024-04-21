use backend::schemas;

fn main() {
    let organizator = schemas::Organizator::default();
    println!("{organizator:?}");
}
