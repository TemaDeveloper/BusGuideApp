use std::fs;
use std::path::Path;
use prost_build::Config;

fn main() -> anyhow::Result<()> {
    // Ensure the schemas directory exists
    let schemas_dir = "schemas";
    if !Path::new(schemas_dir).exists() {
        fs::create_dir(schemas_dir)?;
    }

    let mut config = Config::new();
    config.type_attribute(".", "#[derive(serde::Serialize, serde::Deserialize)]");
    config.compile_protos(
        &["schemas/organizator.proto"],
        &["."]
    )?;

    Ok(())
}
