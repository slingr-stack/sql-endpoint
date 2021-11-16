---
title: SQL endpoint
keywords: 
last_updated: November 2, 2018
tags: []
summary: "Detailed description of the API of SQL endpoint."
---

## Overview

The SQL endpoint allows to run standard SQL queries over SQL databases. This endpoint has been developed with Java and
implements [Java Database Connectivity (JDBC) API](https://www.oracle.com/technetwork/java/javase/jdbc/index.html).


The supported SQL databases and his corresponding JDBC driver version are:

- [MySQL (8.0.13)](https://dev.mysql.com/doc/relnotes/connector-j/8.0/en/news-8-0-13.html)
- [PostgreSQL (9.4-1206-jdbc42)](https://jdbc.postgresql.org/download.html)
- [SQL Server (6.4.0.jre8)](https://docs.microsoft.com/en-us/sql/connect/jdbc/release-notes-for-the-jdbc-driver?view=sql-server-2017#updates-in-microsoft-jdbc-driver-64-for-sql-server)
- [SQLite (3.25.2)](https://www.sqlite.org/releaselog/3_25_2.html)
- [MariaDB (2.1.2)](https://mariadb.com/kb/en/library/mariadb-connector-j-212-release-notes/)
- [Oracle (10.2.0.3.0)](https://www.oracle.com/technetwork/database/enterprise-edition/readme-jdbc-10203-085004.html)

## Quick start

Having a MySQL endpoint with name `sql`, you can run a select query like this:

```js
var rsEmployees = app.endpoints.sql.query("SELECT * FROM employees");
```

## Configuration

The needed information it's the same that you need for configuring a JDBC connector.

### Database type

Select one of the supported SQL databases.

### DB User

This is the database user that the endpoint will use to connect to the database.

### DB password

This is the database user password that the endpoint will use to connect to the database.

### DB Connection String

The JDBC connection string. Each database type has his own connection string. For example, on a MySQL
database your connection string will look like `jdbc:mysql://HOST/DATABASE` and for a SQL server it can
look like this `jdbc:microsoft:sqlserver://HOST:1433;DatabaseName=DATABASE`.

On the connection string we can set some additional options for the database. For instance, since MariaDB
aims to be a drop-in replacement for MySql, the driver permits connection strings beginning with `jdbc:mariadb`
or `jdbc:mysql`. So for specifically use MySQL driver on a MySQL database we have to set the option `disableMariaDbDriver`
 like this `jdbc:mysql://HOST:3306/db?disableMariaDbDriver`

### Maximum Pool Size

This property controls the maximum size that the pool is allowed to reach, including both idle and in-use connections.

## Javascript API

The Javascript API provides only one method.

### query

You can make a `SELECT`, `INSERT`, `UPDATE` or `DELETE` like this:

```js
//SELECT
var rsEmployees = app.endpoints.sql.query("SELECT * FROM employees");
if (rsEmployees !== null) {
  rsEmployees.forEach(function (employee) {
    sys.logs.info('id: ' + employee.id);
    sys.logs.info('name: ' + employee.name);
    sys.logs.info('age: ' + employee.age);
  });
}
```

```js
//INSERT
var response = app.endpoints.sql.query("INSERT INTO employees (id, name, age, address) VALUES ('1004', 'Jonny Deep', '45', '');
```

```js
//DELETE
var response = app.endpoints.sql.query("DELETE FROM employees WHERE employees.id = 1004");
```

## About SLINGR

SLINGR is a low-code rapid application development platform that accelerates development, with robust architecture for integrations and executing custom workflows and automation.

[More info about SLINGR](https://slingr.io)

## License

This endpoint is licensed under the Apache License 2.0. See the `LICENSE` file for more details.



