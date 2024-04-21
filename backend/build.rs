use std::{path::Path, fs};
use anyhow::anyhow;

fn main() -> anyhow::Result<()> {
    // Ensure the schemas directory exists
    let schemas_dir = "schemas";
    if !Path::new(schemas_dir).exists() {
        fs::create_dir(schemas_dir)?;
    }

    let err = prost_build::compile_protos(
        &["schemas/organizator.proto"],
        &["."]
    );

    if let Err(err) = err {
        eprintln!("{}", err.to_string());
        return Err(anyhow!("Compile Error"));
    }

    Ok(())
}
