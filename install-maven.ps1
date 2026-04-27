# Script para instalar Maven automaticamente no Windows
# Execute como Administrator: Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser

$ErrorActionPreference = "Stop"

Write-Host "╔════════════════════════════════════════════════╗" -ForegroundColor Cyan
Write-Host "║  Instalador Maven - Gestor de Estoque         ║" -ForegroundColor Cyan
Write-Host "╚════════════════════════════════════════════════╝" -ForegroundColor Cyan
Write-Host ""

# Verificar se é Admin
$isAdmin = ([Security.Principal.WindowsPrincipal] [Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator")
if (-not $isAdmin) {
    Write-Host "❌ ERRO: Execute this script as Administrator!" -ForegroundColor Red
    Write-Host ""
    Write-Host "How to run as Admin:" -ForegroundColor Yellow
    Write-Host "1. Press Win + X" -ForegroundColor Yellow
    Write-Host "2. Select: Windows PowerShell (Admin)" -ForegroundColor Yellow
    Write-Host "3. Run: Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser" -ForegroundColor Yellow
    Write-Host "4. Run this script again" -ForegroundColor Yellow
    exit 1
}

Write-Host "✅ Running as Administrator" -ForegroundColor Green
Write-Host ""

# Step 1: Check Java
Write-Host "Step 1: Checking Java..." -ForegroundColor Yellow
try {
    $javaVersion = java -version 2>&1
    Write-Host "✅ Java found:" -ForegroundColor Green
    Write-Host $javaVersion[0]
} catch {
    Write-Host "❌ Java not found! Install Java 17+ first" -ForegroundColor Red
    Write-Host "Download: https://www.oracle.com/java/technologies/javase-jdk21-downloads.html" -ForegroundColor Yellow
    exit 1
}

Write-Host ""

# Step 2: Check if Maven already installed
Write-Host "Step 2: Checking for existing Maven..." -ForegroundColor Yellow
$mavenPath = "C:\apache-maven-3.9.8"
if (Test-Path $mavenPath) {
    Write-Host "✅ Maven already exists at: $mavenPath" -ForegroundColor Green
} else {
    Write-Host "⏳ Downloading Maven..." -ForegroundColor Yellow

    $downloadUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.8/binaries/apache-maven-3.9.8-bin.zip"
    $zipPath = "C:\maven.zip"

    try {
        Invoke-WebRequest -Uri $downloadUrl -OutFile $zipPath -UseBasicParsing
        Write-Host "✅ Downloaded Maven" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to download Maven" -ForegroundColor Red
        Write-Host "Error: $_" -ForegroundColor Red
        exit 1
    }

    Write-Host "⏳ Extracting Maven..." -ForegroundColor Yellow
    try {
        Expand-Archive -Path $zipPath -DestinationPath "C:\" -Force
        Remove-Item $zipPath
        Write-Host "✅ Extracted Maven" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to extract Maven" -ForegroundColor Red
        Write-Host "Error: $_" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""

# Step 3: Add to PATH
Write-Host "Step 3: Adding Maven to PATH..." -ForegroundColor Yellow

$mavenBinPath = "$mavenPath\bin"

# Check if already in PATH
$currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
if ($currentPath -like "*$mavenBinPath*") {
    Write-Host "✅ Maven bin already in PATH" -ForegroundColor Green
} else {
    try {
        $newPath = "$currentPath;$mavenBinPath"
        [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
        Write-Host "✅ Added Maven to PATH" -ForegroundColor Green
    } catch {
        Write-Host "❌ Failed to add Maven to PATH" -ForegroundColor Red
        Write-Host "Error: $_" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""

# Step 4: Verify Installation
Write-Host "Step 4: Verifying installation..." -ForegroundColor Yellow

# Refresh PATH in current session
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

$output = & $mavenPath\bin\mvn.cmd --version 2>&1
if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Maven installed successfully!" -ForegroundColor Green
    Write-Host ""
    Write-Host $output
} else {
    Write-Host "❌ Maven verification failed" -ForegroundColor Red
    Write-Host "Error: $output" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "╔════════════════════════════════════════════════╗" -ForegroundColor Green
Write-Host "║  ✅ MAVEN INSTALLATION COMPLETE               ║" -ForegroundColor Green
Write-Host "╚════════════════════════════════════════════════╝" -ForegroundColor Green

Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Close and reopen PowerShell" -ForegroundColor Cyan
Write-Host "2. Navigate to project: cd D:\projects\intelliji\GestorEstoquesWeb" -ForegroundColor Cyan
Write-Host "3. Run tests: mvn clean test" -ForegroundColor Cyan
Write-Host ""
Write-Host "Quick test commands:" -ForegroundColor Yellow
Write-Host "  mvn clean test                              # Run all tests" -ForegroundColor Yellow
Write-Host "  mvn test -Dtest=ProdutoServiceTest         # Run specific test" -ForegroundColor Yellow
Write-Host "  mvn clean test jacoco:report               # Generate coverage report" -ForegroundColor Yellow

Write-Host ""

