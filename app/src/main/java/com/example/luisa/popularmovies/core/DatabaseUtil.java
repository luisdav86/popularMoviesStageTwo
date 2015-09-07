package com.example.luisa.popularmovies.core;

import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.example.luisa.popularmovies.entity.Movie;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by LuisA on 8/28/2015.
 */
public class DatabaseUtil {

    private static final String STATEMENT_CREATE_TABLE = " CREATE TABLE  %s ( ";

    private static final String STATEMENT_PRIMARY_KEY_AUTOINCREMENT = " PRIMARY KEY AUTOINCREMENT ";

    private static final String STATEMENT_PRIMARY_KEY = " PRIMARY KEY AUTOINCREMENT ";

    private static final String STATEMENT_FOREIGN_KEY = " REFERENCES  %s ( %s ) ";

    private static final String ON_CASCADE_DELETE = " ON DELETE CASCADE ";

    private static final String STATEMENT_NOT_NULL = " NOT NULL ";

    private static final String STATEMENT_UNIQUE = " UNIQUE ";

    public static void createTable(Class<?> clasz, SQLiteDatabase db) {
        DatabaseTable databaseTable = clasz.getAnnotation(DatabaseTable.class);
        createTable(clasz, databaseTable.name(), db);
    }

    public static void loadClassFields(Class<?> clasz, List<Field> fields) {

        fields.addAll(Arrays.asList(clasz.getDeclaredFields()));

        if (clasz == DataAccessObject.class) {
            return;
        }

        loadClassFields(clasz.getSuperclass(), fields);

    }

    private static String getDatabaseFieldType(Field field) {
        // Get the field type
        Type type = field.getType();

        // Assign the value depending on the field type
        if (type == int.class) {
            return DatabaseFieldType.INTEGER;
        }

        if (type == short.class) {
            return DatabaseFieldType.INTEGER;
        }

        if (type == long.class) {
            return DatabaseFieldType.INTEGER;
        }

        if (type == float.class) {
            return DatabaseFieldType.NUMERIC;
        }

        if (type == double.class) {
            return DatabaseFieldType.NUMERIC;
        }

        if (type == String.class) {
            return DatabaseFieldType.TEXT;
        }

        if (type == byte.class) {
            return DatabaseFieldType.BLOB;
        }

        if (type == boolean.class) {
            return DatabaseFieldType.INTEGER;
        }

        if (type == Date.class) {
            return DatabaseFieldType.TEXT;
        }

        if (type instanceof Class && ((Class<?>) type).isEnum()) {
            return DatabaseFieldType.INTEGER;
        }

        return DatabaseFieldType.TEXT;

    }

    private static String buildFieldCreateStatement(Field field) {

        DatabaseField databaseField = field.getAnnotation(DatabaseField.class);

        if (databaseField != null) {
            // Add the field and the type
            StringBuilder statement = new StringBuilder(String.format(
                    " %s %s ", databaseField.name(),
                    getDatabaseFieldType(field)));

            // Add the primary key constraint.
            if (databaseField.primaryKey()) {
                statement
                        .append(databaseField.autoincrement() ? STATEMENT_PRIMARY_KEY_AUTOINCREMENT
                                : STATEMENT_PRIMARY_KEY);
            } else if (databaseField.foreignKey()) {

                // Add a foreign key constraint.
                DatabaseFieldReference reference = field
                        .getAnnotation(DatabaseFieldReference.class);

                // Gets the reference table and the field.
                DatabaseTable referenceTable = reference.table().getAnnotation(
                        DatabaseTable.class);
                DatabaseField referenceField = reference.field();

                // Add a foreign key constraint and the reference of the table and the field.
                statement.append(String.format(STATEMENT_FOREIGN_KEY,
                        referenceTable.name(), referenceField.name()));
                if (databaseField.onCascadeDelete()) {
                    statement.append(ON_CASCADE_DELETE);
                }
            }

            // Add a not null constraint.
            if (databaseField.notNull()) {
                statement.append(STATEMENT_NOT_NULL);
            }

            // Add a unique constraint.
            if (databaseField.unique()) {
                statement.append(STATEMENT_UNIQUE);
            }

            return statement.toString();
        }

        return null;
    }

    private static String buildFieldsCreateStatement(Class<?> clasz) {
        List<Field> fields = new ArrayList<Field>();
        List<String> statements = new ArrayList<String>();

        loadClassFields(clasz, fields);

        //StringBuilder statement = new StringBuilder();

        ListIterator<Field> iterator = fields.listIterator();

        while (iterator.hasNext()) {
            Field field = iterator.next();

            String fieldStatement = buildFieldCreateStatement(field);
            if (fieldStatement != null) {
                statements.add(fieldStatement);
            }
        }

        return TextUtils.join(",", statements);
    }

    public static void createTable(Class<?> clasz, String tableName,
                                   SQLiteDatabase db) {
        StringBuilder command = new StringBuilder();

        // Add the create table statement.
        command.append(String.format(STATEMENT_CREATE_TABLE, tableName));
        // Append the table fields.
        command.append(buildFieldsCreateStatement(clasz));
        // Close the create table statement.
        command.append(" ); ");

        db.execSQL(command.toString());
    }

    interface DatabaseFieldType {

        String BLOB = "BLOB";

        String INTEGER = "INTEGER";

        String NUMERIC = "NUMERIC";

        String TEXT = "TEXT";
    }

}
