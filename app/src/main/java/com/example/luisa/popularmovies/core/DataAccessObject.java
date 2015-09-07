package com.example.luisa.popularmovies.core;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.example.luisa.popularmovies.MoviesApp;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by LuisA on 8/28/2015.
 */
public class DataAccessObject {

    public static final long DEFAULT_ID = -1;

    private static SQLiteDatabase db = null;

    @DatabaseField(name = BaseColumns._ID, primaryKey = true, autoincrement = true)
    @SerializedName("id")
    @Expose
    protected long id = DEFAULT_ID;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public static ICoreDb getHelper() {
        return MoviesApp.getInstance().getHelper();
    }

    public static int bulkInsert(List<? extends BaseBusinessObject> objects) {
        String tableName = "";
        SQLiteDatabase db = getDatabase();

        if (objects.size() > 0) {
            tableName = objects.get(0).getTableName();
            int returnCount = 0;
            try {
                db.beginTransaction();
                for (BaseBusinessObject object : objects) {
                    long _id = db.insert(tableName, null, object.getContentValues());
                    if (_id != -1) {
                        returnCount++;
                    }
                }
                db.setTransactionSuccessful();
                return returnCount;
            } catch (Exception e) {
                LogIt.e(DataAccessObject.class, e, e.getMessage());
            } finally {
                db.endTransaction();
                getHelper().close();
            }
        }
        return 0;
    }

    public static ContentValues[] toContentValues(List<? extends BaseBusinessObject> objects) {
        if (objects == null) {
            return null;
        }
        ContentValues[] values = new ContentValues[objects.size()];
        for (int i = 0; i < objects.size(); i++) {
            values[i] = objects.get(i).getContentValues();
        }
        return values;
    }

    public static int delete(Class<?> clasz, String selection, String[] selectionArgs) {
        return delete(getTableName(clasz), selection, selectionArgs);
    }

    public static int delete(String tableName, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getDatabase();
        return db.delete(tableName, selection, selectionArgs);
    }

    public static int update(Class<?> clasz, ContentValues values, String selection, String[] selectionArgs) {
        return update(getTableName(clasz), values, selection, selectionArgs);
    }

    public static int update(String tableName, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = getDatabase();
        return db.update(tableName, values, selection, selectionArgs);
    }


    public static int bulkInsert(Class<?> clasz, ContentValues[] values) {
        return bulkInsert(getTableName(clasz), values);
    }

    public static int bulkInsert(String tableName, ContentValues[] values) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return returnCount;
    }

    public static long insert(Class<?> clasz, ContentValues contentValues) {
        return insert(getTableName(clasz), contentValues);
    }

    public static long insert(String tableName, ContentValues contentValues) {
        SQLiteDatabase db = getDatabase();
        return db.insert(tableName, null, contentValues);
    }

    protected ContentValues getContentValues() {
        ContentValues contentValues = new ContentValues();
        for (Field field : getFields()) {
            DatabaseField databaseField = field
                    .getAnnotation(DatabaseField.class);
            if (databaseField != null) {
                try {
                    if (!field.getName().equals("id") || id != DEFAULT_ID) {
                        field.setAccessible(true);
                        bindFieldValue(contentValues, databaseField, field);
                    }
                } catch (IllegalArgumentException e) {
                    LogIt.e(this, e, e.getMessage());
                } catch (IllegalAccessException e) {
                    LogIt.e(this, e, e.getMessage());
                }
            }
        }
        return contentValues;
    }

    private static SQLiteDatabase getDatabase() {
        if (db == null) {
            db = getHelper().openDataBase(SQLiteDatabase.OPEN_READWRITE);
        }
        return db;
    }

    private void bindFieldValue(ContentValues contentValues,
                                DatabaseField databaseField, Field field)
            throws IllegalArgumentException, IllegalAccessException {
        // Get the field type
        Type type = field.getType();

        // Assign the value depending on the field type
        if (type == int.class) {
            int value = field.getInt(this);
            // Check if is a valid foreign key id
            if (databaseField.foreignKey() && value <= 0) {
                contentValues.putNull(databaseField.name());
            } else {
                contentValues.put(databaseField.name(), field.getInt(this));

            }
            return;
        }

        if (type == short.class) {
            contentValues.put(databaseField.name(), field.getShort(this));
            return;
        }

        if (type == long.class) {
            long value = field.getLong(this);
            // Check if is a valid foreign key id
            if (databaseField.foreignKey() && value <= 0) {
                contentValues.putNull(databaseField.name());
            } else {
                contentValues.put(databaseField.name(), value);
            }
            return;
        }

        if (type == float.class) {
            contentValues.put(databaseField.name(), field.getFloat(this));
            return;
        }

        if (type == double.class) {
            contentValues.put(databaseField.name(), field.getDouble(this));
            return;
        }

        if (type == String.class) {
            contentValues.put(databaseField.name(), (String) field.get(this));
            return;
        }

        if (type == byte.class) {
            contentValues.put(databaseField.name(), field.getByte(this));
            return;
        }

        if (type == boolean.class) {
            contentValues.put(databaseField.name(), field.getBoolean(this));
            return;
        }

        if (type == Date.class) {
            contentValues.put(databaseField.name(),
                    DateUtil.formatISO8601((Date) field.get(this)));
            return;
        }

    }

    public static <T extends DataAccessObject> List<T> findAll(Class<T> clasz) {
        return find(clasz, null, null, null, null, null, null, null);
    }

    public static String getTableName(Class<?> clasz) {
        DatabaseTable databaseTable = clasz.getAnnotation(DatabaseTable.class);
        return databaseTable.name();
    }

    public String getTableName() {
        return getTableName(getClass());
    }

    protected static <T extends DataAccessObject> List<T> find(Class<T> clasz,
                                                               String[] columns, String selection, String[] selectionArgs,
                                                               String groupBy, String having, String orderBy, String limit) {
        List<T> list = new ArrayList<T>();
        Cursor cursor = null;

        try {
            cursor = getHelper().openDataBase().query(getTableName(clasz),
                    columns, selection, selectionArgs, groupBy, having,
                    orderBy, limit);
            list = mapItems(cursor, clasz);
        } catch (Exception e) {
            LogIt.e(DataAccessObject.class, e, e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            getHelper().close();
        }
        return list;
    }

    private static <T extends DataAccessObject> List<T> mapItems(Cursor cursor,
                                                                 Class<T> clasz) throws InstantiationException,
            IllegalAccessException {
        List<T> list = new ArrayList<T>();
        while (cursor != null && cursor.moveToNext()) {
            list.add(mapItem(cursor, clasz));
        }
        return list;
    }

    public static <T extends DataAccessObject> T mapItem(Cursor cursor,
                                                          Class<T> clasz) throws InstantiationException,
            IllegalAccessException {
        T item = null;
        item = clasz.newInstance();
        item.mapFromCursor(cursor);
        return item;
    }

    public void mapFromCursor(Cursor cursor) {
        for (Field field : getFields()) {
            DatabaseField databaseField = field
                    .getAnnotation(DatabaseField.class);
            if (databaseField != null) {
                try {
                    setFieldValue(field, databaseField, cursor);
                } catch (IllegalArgumentException e) {
                    LogIt.e(this, e, e.getMessage());
                } catch (IllegalAccessException e) {
                    LogIt.e(this, e, e.getMessage());
                } catch (ParseException e) {
                    LogIt.e(this, e, e.getMessage());
                }
            }
        }
    }

    private void setFieldValue(Field field, DatabaseField databaseField,
                               Cursor cursor) throws IllegalArgumentException,
            IllegalAccessException, ParseException {
        // Get the field type
        Type type = field.getType();
        field.setAccessible(true);

        // Gets the column index from the cursor.
        int columnIndex = cursor.getColumnIndex(databaseField.name());

        // Checks if the column index is valid.
        if (columnIndex < 0) {
            return;
        }

        // Assign the value depending on the field type
        if (type == int.class) {
            field.set(this, cursor.getInt(columnIndex));
            return;
        }

        if (type == short.class) {
            field.set(this, cursor.getShort(columnIndex));
            return;
        }

        if (type == long.class) {
            field.set(this, cursor.getLong(columnIndex));
            return;
        }

        if (type == float.class) {
            field.set(this, cursor.getFloat(columnIndex));
            return;
        }

        if (type == double.class) {
            field.set(this, cursor.getDouble(columnIndex));
            return;
        }

        if (type == String.class) {
            field.set(this, cursor.getString(columnIndex));
            return;
        }

        if (type == byte.class) {
            field.set(this, cursor.getBlob(columnIndex));
            return;
        }

        if (type == boolean.class) {
            field.set(this, cursor.getInt(columnIndex) == 1);
            return;
        }

        if (type == Date.class) {
            field.set(this,
                    DateUtil.parseISO8601(cursor.getString(columnIndex)));
            return;
        }
    }

    protected List<Field> getFields() {
        List<Field> result = new ArrayList<Field>();
        loadClassFields(this.getClass(), result);
        return result;
    }

    public static void loadClassFields(Class<?> clasz, List<Field> fields) {
        fields.addAll(Arrays.asList(clasz.getDeclaredFields()));
        if (clasz == DataAccessObject.class) {
            return;
        }
        loadClassFields(clasz.getSuperclass(), fields);

    }
}
