$ErrorActionPreference = "Stop"

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$projectDir = (Resolve-Path (Join-Path $scriptDir "..")).Path
$envFile = Join-Path $projectDir ".env"

if (Test-Path $envFile) {
    Get-Content $envFile | ForEach-Object {
        $line = $_.Trim()

        if ($line.Length -gt 0 -and -not $line.StartsWith("#")) {
            $parts = $line -split "=", 2
            if ($parts.Count -eq 2) {
                $name = $parts[0].Trim()
                $value = $parts[1]
                Set-Item -Path "Env:$name" -Value $value
            }
        }
    }
}

if (-not $env:DB_URL) {
    $env:DB_URL = "jdbc:mysql://localhost:3306/department_store_inventory?useSSL=false&allowPublicKeyRetrieval=true"
}

if (-not $env:DB_USER) {
    $env:DB_USER = "root"
}

if (-not $env:DB_PASSWORD) {
    $env:DB_PASSWORD = ""
}

Push-Location $projectDir
try {
    mvn clean javafx:run
}
finally {
    Pop-Location
}
