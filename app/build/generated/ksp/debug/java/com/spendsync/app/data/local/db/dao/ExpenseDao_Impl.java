package com.spendsync.app.data.local.db.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.spendsync.app.data.local.db.entities.ExpenseEntity;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ExpenseDao_Impl implements ExpenseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ExpenseEntity> __insertionAdapterOfExpenseEntity;

  private final EntityDeletionOrUpdateAdapter<ExpenseEntity> __updateAdapterOfExpenseEntity;

  private final SharedSQLiteStatement __preparedStmtOfDeleteById;

  private final SharedSQLiteStatement __preparedStmtOfMarkNotionSynced;

  private final SharedSQLiteStatement __preparedStmtOfMarkGoogleSynced;

  private final SharedSQLiteStatement __preparedStmtOfRecordSyncError;

  public ExpenseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfExpenseEntity = new EntityInsertionAdapter<ExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `expenses` (`id`,`name`,`amount`,`categoryId`,`paymentMethodId`,`date`,`notionPageId`,`isSynced`,`notionSynced`,`googleSynced`,`lastSyncError`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        statement.bindString(4, entity.getCategoryId());
        if (entity.getPaymentMethodId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPaymentMethodId());
        }
        statement.bindLong(6, entity.getDate());
        if (entity.getNotionPageId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getNotionPageId());
        }
        final int _tmp = entity.isSynced() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.getNotionSynced() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.getGoogleSynced() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getLastSyncError() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getLastSyncError());
        }
        statement.bindLong(12, entity.getCreatedAt());
      }
    };
    this.__updateAdapterOfExpenseEntity = new EntityDeletionOrUpdateAdapter<ExpenseEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `expenses` SET `id` = ?,`name` = ?,`amount` = ?,`categoryId` = ?,`paymentMethodId` = ?,`date` = ?,`notionPageId` = ?,`isSynced` = ?,`notionSynced` = ?,`googleSynced` = ?,`lastSyncError` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ExpenseEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindDouble(3, entity.getAmount());
        statement.bindString(4, entity.getCategoryId());
        if (entity.getPaymentMethodId() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getPaymentMethodId());
        }
        statement.bindLong(6, entity.getDate());
        if (entity.getNotionPageId() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getNotionPageId());
        }
        final int _tmp = entity.isSynced() ? 1 : 0;
        statement.bindLong(8, _tmp);
        final int _tmp_1 = entity.getNotionSynced() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
        final int _tmp_2 = entity.getGoogleSynced() ? 1 : 0;
        statement.bindLong(10, _tmp_2);
        if (entity.getLastSyncError() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getLastSyncError());
        }
        statement.bindLong(12, entity.getCreatedAt());
        statement.bindString(13, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM expenses WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkNotionSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE expenses SET notionPageId = ?, notionSynced = 1, isSynced = 1, lastSyncError = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkGoogleSynced = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE expenses SET googleSynced = 1, isSynced = 1, lastSyncError = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRecordSyncError = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE expenses SET lastSyncError = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ExpenseEntity expense, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfExpenseEntity.insert(expense);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final ExpenseEntity expense, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfExpenseEntity.handle(expense);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteById(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteById.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markNotionSynced(final String id, final String pageId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkNotionSynced.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, pageId);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkNotionSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markGoogleSynced(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkGoogleSynced.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkGoogleSynced.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object recordSyncError(final String id, final String message,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRecordSyncError.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, message);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRecordSyncError.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExpenseEntity>> getAllExpenses() {
    final String _sql = "SELECT * FROM expenses ORDER BY date DESC, createdAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expenses"}, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPaymentMethodId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethodId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotionPageId = CursorUtil.getColumnIndexOrThrow(_cursor, "notionPageId");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfNotionSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "notionSynced");
          final int _cursorIndexOfGoogleSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "googleSynced");
          final int _cursorIndexOfLastSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncError");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCategoryId;
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            final String _tmpPaymentMethodId;
            if (_cursor.isNull(_cursorIndexOfPaymentMethodId)) {
              _tmpPaymentMethodId = null;
            } else {
              _tmpPaymentMethodId = _cursor.getString(_cursorIndexOfPaymentMethodId);
            }
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpNotionPageId;
            if (_cursor.isNull(_cursorIndexOfNotionPageId)) {
              _tmpNotionPageId = null;
            } else {
              _tmpNotionPageId = _cursor.getString(_cursorIndexOfNotionPageId);
            }
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            final boolean _tmpNotionSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotionSynced);
            _tmpNotionSynced = _tmp_1 != 0;
            final boolean _tmpGoogleSynced;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfGoogleSynced);
            _tmpGoogleSynced = _tmp_2 != 0;
            final String _tmpLastSyncError;
            if (_cursor.isNull(_cursorIndexOfLastSyncError)) {
              _tmpLastSyncError = null;
            } else {
              _tmpLastSyncError = _cursor.getString(_cursorIndexOfLastSyncError);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpName,_tmpAmount,_tmpCategoryId,_tmpPaymentMethodId,_tmpDate,_tmpNotionPageId,_tmpIsSynced,_tmpNotionSynced,_tmpGoogleSynced,_tmpLastSyncError,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getExpenseById(final String id,
      final Continuation<? super ExpenseEntity> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ExpenseEntity>() {
      @Override
      @Nullable
      public ExpenseEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPaymentMethodId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethodId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotionPageId = CursorUtil.getColumnIndexOrThrow(_cursor, "notionPageId");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfNotionSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "notionSynced");
          final int _cursorIndexOfGoogleSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "googleSynced");
          final int _cursorIndexOfLastSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncError");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final ExpenseEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCategoryId;
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            final String _tmpPaymentMethodId;
            if (_cursor.isNull(_cursorIndexOfPaymentMethodId)) {
              _tmpPaymentMethodId = null;
            } else {
              _tmpPaymentMethodId = _cursor.getString(_cursorIndexOfPaymentMethodId);
            }
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpNotionPageId;
            if (_cursor.isNull(_cursorIndexOfNotionPageId)) {
              _tmpNotionPageId = null;
            } else {
              _tmpNotionPageId = _cursor.getString(_cursorIndexOfNotionPageId);
            }
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            final boolean _tmpNotionSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotionSynced);
            _tmpNotionSynced = _tmp_1 != 0;
            final boolean _tmpGoogleSynced;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfGoogleSynced);
            _tmpGoogleSynced = _tmp_2 != 0;
            final String _tmpLastSyncError;
            if (_cursor.isNull(_cursorIndexOfLastSyncError)) {
              _tmpLastSyncError = null;
            } else {
              _tmpLastSyncError = _cursor.getString(_cursorIndexOfLastSyncError);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _result = new ExpenseEntity(_tmpId,_tmpName,_tmpAmount,_tmpCategoryId,_tmpPaymentMethodId,_tmpDate,_tmpNotionPageId,_tmpIsSynced,_tmpNotionSynced,_tmpGoogleSynced,_tmpLastSyncError,_tmpCreatedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPendingNotionExpenses(
      final Continuation<? super List<ExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE notionSynced = 0 ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPaymentMethodId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethodId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotionPageId = CursorUtil.getColumnIndexOrThrow(_cursor, "notionPageId");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfNotionSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "notionSynced");
          final int _cursorIndexOfGoogleSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "googleSynced");
          final int _cursorIndexOfLastSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncError");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCategoryId;
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            final String _tmpPaymentMethodId;
            if (_cursor.isNull(_cursorIndexOfPaymentMethodId)) {
              _tmpPaymentMethodId = null;
            } else {
              _tmpPaymentMethodId = _cursor.getString(_cursorIndexOfPaymentMethodId);
            }
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpNotionPageId;
            if (_cursor.isNull(_cursorIndexOfNotionPageId)) {
              _tmpNotionPageId = null;
            } else {
              _tmpNotionPageId = _cursor.getString(_cursorIndexOfNotionPageId);
            }
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            final boolean _tmpNotionSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotionSynced);
            _tmpNotionSynced = _tmp_1 != 0;
            final boolean _tmpGoogleSynced;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfGoogleSynced);
            _tmpGoogleSynced = _tmp_2 != 0;
            final String _tmpLastSyncError;
            if (_cursor.isNull(_cursorIndexOfLastSyncError)) {
              _tmpLastSyncError = null;
            } else {
              _tmpLastSyncError = _cursor.getString(_cursorIndexOfLastSyncError);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpName,_tmpAmount,_tmpCategoryId,_tmpPaymentMethodId,_tmpDate,_tmpNotionPageId,_tmpIsSynced,_tmpNotionSynced,_tmpGoogleSynced,_tmpLastSyncError,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getPendingGoogleExpenses(
      final Continuation<? super List<ExpenseEntity>> $completion) {
    final String _sql = "SELECT * FROM expenses WHERE googleSynced = 0 ORDER BY createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPaymentMethodId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethodId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotionPageId = CursorUtil.getColumnIndexOrThrow(_cursor, "notionPageId");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfNotionSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "notionSynced");
          final int _cursorIndexOfGoogleSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "googleSynced");
          final int _cursorIndexOfLastSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncError");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCategoryId;
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            final String _tmpPaymentMethodId;
            if (_cursor.isNull(_cursorIndexOfPaymentMethodId)) {
              _tmpPaymentMethodId = null;
            } else {
              _tmpPaymentMethodId = _cursor.getString(_cursorIndexOfPaymentMethodId);
            }
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpNotionPageId;
            if (_cursor.isNull(_cursorIndexOfNotionPageId)) {
              _tmpNotionPageId = null;
            } else {
              _tmpNotionPageId = _cursor.getString(_cursorIndexOfNotionPageId);
            }
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            final boolean _tmpNotionSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotionSynced);
            _tmpNotionSynced = _tmp_1 != 0;
            final boolean _tmpGoogleSynced;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfGoogleSynced);
            _tmpGoogleSynced = _tmp_2 != 0;
            final String _tmpLastSyncError;
            if (_cursor.isNull(_cursorIndexOfLastSyncError)) {
              _tmpLastSyncError = null;
            } else {
              _tmpLastSyncError = _cursor.getString(_cursorIndexOfLastSyncError);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpName,_tmpAmount,_tmpCategoryId,_tmpPaymentMethodId,_tmpDate,_tmpNotionPageId,_tmpIsSynced,_tmpNotionSynced,_tmpGoogleSynced,_tmpLastSyncError,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ExpenseEntity>> getFullSuggestions(final String prefix) {
    final String _sql = "SELECT * FROM expenses WHERE name LIKE ? || '%' GROUP BY name ORDER BY createdAt DESC LIMIT 5";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, prefix);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expenses"}, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPaymentMethodId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethodId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotionPageId = CursorUtil.getColumnIndexOrThrow(_cursor, "notionPageId");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfNotionSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "notionSynced");
          final int _cursorIndexOfGoogleSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "googleSynced");
          final int _cursorIndexOfLastSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncError");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCategoryId;
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            final String _tmpPaymentMethodId;
            if (_cursor.isNull(_cursorIndexOfPaymentMethodId)) {
              _tmpPaymentMethodId = null;
            } else {
              _tmpPaymentMethodId = _cursor.getString(_cursorIndexOfPaymentMethodId);
            }
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpNotionPageId;
            if (_cursor.isNull(_cursorIndexOfNotionPageId)) {
              _tmpNotionPageId = null;
            } else {
              _tmpNotionPageId = _cursor.getString(_cursorIndexOfNotionPageId);
            }
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            final boolean _tmpNotionSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotionSynced);
            _tmpNotionSynced = _tmp_1 != 0;
            final boolean _tmpGoogleSynced;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfGoogleSynced);
            _tmpGoogleSynced = _tmp_2 != 0;
            final String _tmpLastSyncError;
            if (_cursor.isNull(_cursorIndexOfLastSyncError)) {
              _tmpLastSyncError = null;
            } else {
              _tmpLastSyncError = _cursor.getString(_cursorIndexOfLastSyncError);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpName,_tmpAmount,_tmpCategoryId,_tmpPaymentMethodId,_tmpDate,_tmpNotionPageId,_tmpIsSynced,_tmpNotionSynced,_tmpGoogleSynced,_tmpLastSyncError,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ExpenseEntity>> getExpensesByDateRange(final long startDate,
      final long endDate) {
    final String _sql = "SELECT * FROM expenses WHERE date >= ? AND date <= ? ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    _argIndex = 2;
    _statement.bindLong(_argIndex, endDate);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"expenses"}, new Callable<List<ExpenseEntity>>() {
      @Override
      @NonNull
      public List<ExpenseEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfAmount = CursorUtil.getColumnIndexOrThrow(_cursor, "amount");
          final int _cursorIndexOfCategoryId = CursorUtil.getColumnIndexOrThrow(_cursor, "categoryId");
          final int _cursorIndexOfPaymentMethodId = CursorUtil.getColumnIndexOrThrow(_cursor, "paymentMethodId");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfNotionPageId = CursorUtil.getColumnIndexOrThrow(_cursor, "notionPageId");
          final int _cursorIndexOfIsSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "isSynced");
          final int _cursorIndexOfNotionSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "notionSynced");
          final int _cursorIndexOfGoogleSynced = CursorUtil.getColumnIndexOrThrow(_cursor, "googleSynced");
          final int _cursorIndexOfLastSyncError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSyncError");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final List<ExpenseEntity> _result = new ArrayList<ExpenseEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ExpenseEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final double _tmpAmount;
            _tmpAmount = _cursor.getDouble(_cursorIndexOfAmount);
            final String _tmpCategoryId;
            _tmpCategoryId = _cursor.getString(_cursorIndexOfCategoryId);
            final String _tmpPaymentMethodId;
            if (_cursor.isNull(_cursorIndexOfPaymentMethodId)) {
              _tmpPaymentMethodId = null;
            } else {
              _tmpPaymentMethodId = _cursor.getString(_cursorIndexOfPaymentMethodId);
            }
            final long _tmpDate;
            _tmpDate = _cursor.getLong(_cursorIndexOfDate);
            final String _tmpNotionPageId;
            if (_cursor.isNull(_cursorIndexOfNotionPageId)) {
              _tmpNotionPageId = null;
            } else {
              _tmpNotionPageId = _cursor.getString(_cursorIndexOfNotionPageId);
            }
            final boolean _tmpIsSynced;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsSynced);
            _tmpIsSynced = _tmp != 0;
            final boolean _tmpNotionSynced;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfNotionSynced);
            _tmpNotionSynced = _tmp_1 != 0;
            final boolean _tmpGoogleSynced;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfGoogleSynced);
            _tmpGoogleSynced = _tmp_2 != 0;
            final String _tmpLastSyncError;
            if (_cursor.isNull(_cursorIndexOfLastSyncError)) {
              _tmpLastSyncError = null;
            } else {
              _tmpLastSyncError = _cursor.getString(_cursorIndexOfLastSyncError);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            _item = new ExpenseEntity(_tmpId,_tmpName,_tmpAmount,_tmpCategoryId,_tmpPaymentMethodId,_tmpDate,_tmpNotionPageId,_tmpIsSynced,_tmpNotionSynced,_tmpGoogleSynced,_tmpLastSyncError,_tmpCreatedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getTotalSpending(final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(amount) FROM expenses";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getTotalSpendingSince(final long startDate,
      final Continuation<? super Double> $completion) {
    final String _sql = "SELECT SUM(amount) FROM expenses WHERE date >= ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, startDate);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Double>() {
      @Override
      @Nullable
      public Double call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Double _result;
          if (_cursor.moveToFirst()) {
            final Double _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getDouble(0);
            }
            _result = _tmp;
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
