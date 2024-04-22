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
    /* 
     * those derives are there to do type reflection which is necessary for external libraries to
     * know how our structs/classes look like, generically, this is called reflection
     */  
    config.type_attribute(".", "#[derive(serde::Serialize, serde::Deserialize, sqlx::FromRow)]");
    /* 
     * here we access schemas and transform them into struct defenitions, it is usefull
     * since this allows us to have cross-language class defenitions
     */  
    config.compile_protos(
        &["schemas/organizator.proto"],
        &[".."]
    )?;

    Ok(())
}
