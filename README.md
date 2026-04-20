# CS3560 Project

Make sure Apache Maven is installed and available on your terminal `PATH`.

## MySQL setup

Create the database and tables:

```bash
mysql -u root -p < database/schema.sql
```

Optional: load the original three inventory examples into MySQL:

```bash
mysql -u root -p < database/sample-data.sql
```

The app reads these environment variables:

```bash
export DB_URL="jdbc:mysql://localhost:3306/department_store_inventory?useSSL=false&allowPublicKeyRetrieval=true"
export DB_USER="root"
export DB_PASSWORD="your_mysql_password"
```

If you do not set them, the app uses `root` with an empty password on `localhost`.

This project also includes a local `.env` file for those values. If your MySQL root user has a password, update the `DB_PASSWORD` value there. The `.env` file is ignored by Git.

To run the JavaFX inventory dashboard:

```bash
mvn clean javafx:run
```

Or run it through the helper script, which loads `.env` first:

```bash
./scripts/run-dashboard.sh
```
