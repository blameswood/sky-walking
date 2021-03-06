package com.a.eye.skywalking.plugin.jdbc;

import com.a.eye.skywalking.api.context.TracerContext;
import com.a.eye.skywalking.sniffer.mock.context.MockTracerContextListener;
import com.a.eye.skywalking.sniffer.mock.context.SegmentAssert;
import com.a.eye.skywalking.trace.LogData;
import com.a.eye.skywalking.trace.Span;
import com.a.eye.skywalking.trace.TraceSegment;
import com.a.eye.skywalking.trace.tag.Tags;
import com.mysql.cj.api.jdbc.JdbcConnection;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyByte;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SWCallableStatementTest extends AbstractStatementTest {
    @Mock
    private Array array;
    @Mock
    private SQLXML sqlxml;
    @Mock
    private RowId rowId;
    @Mock
    private Ref ref;
    @Mock
    private Clob clob;
    @Mock
    private NClob nClob;
    @Mock
    private Reader reader;
    @Mock
    private InputStream inputStream;
    @Mock
    private Blob blob;
    @Mock
    private com.mysql.cj.jdbc.CallableStatement mysqlCallableStatement;
    @Mock
    private JdbcConnection jdbcConnection;
    private SWConnection swConnection;
    private SWConnection multiHostConnection;
    private byte[] bytesParam = new byte[]{1, 2};

    @Before
    public void setUp() throws Exception {
        mockTracerContextListener = new MockTracerContextListener();
        swConnection = new SWConnection("jdbc:mysql://127.0.0.1:3306/test", new Properties(), jdbcConnection);
        multiHostConnection = new SWConnection("jdbc:mysql://127.0.0.1:3306,127.0.0.1:3309/test", new Properties(), jdbcConnection);

        TracerContext.ListenerManager.add(mockTracerContextListener);

        when(jdbcConnection.prepareCall(anyString())).thenReturn(mysqlCallableStatement);
        when(jdbcConnection.prepareCall(anyString(), anyInt(), anyInt(), anyInt())).thenReturn(mysqlCallableStatement);
        when(jdbcConnection.prepareCall(anyString(), anyInt(), anyInt())).thenReturn(mysqlCallableStatement);
    }

    @Test
    public void testSetParam() throws SQLException, MalformedURLException {
        CallableStatement callableStatement = multiHostConnection.prepareCall("SELECT * FROM test WHERE a = ? or b = ? or c=? or d = ? or e = ?" +
                " or e = ? or f = ? or g = ? or h = ? or i = ? or j = ? or k = ? or l = ? or m = ?  or n = ? or o = ? or p = ? " +
                " or r = ?  or s = ? or t = ?  or u = ?  or v = ?  or w = ?  or x = ?  or y = ? or z = ? or a1 = ? or a2 = ? or a3 = ?" +
                " or a4 = ? or a5 = ? or a6 = ?  or a7 = ?  or a8 = ?  or a9 = ? or b1 = ? or b2 = ? or b3 = ? or b4 = ? or b5 = ?" +
                " or b6 = ? or b7 = ? or b8  = ? or b9 = ? or c1 = ?  or c2 = ? or c3 = ?");
        callableStatement.clearParameters();
        callableStatement.setAsciiStream(1, inputStream);
        callableStatement.setAsciiStream(2, inputStream, 10);
        callableStatement.setAsciiStream(3, inputStream, 1000000L);
        callableStatement.setCharacterStream(4, reader);
        callableStatement.setCharacterStream(4, reader, 10);
        callableStatement.setCharacterStream(5, reader, 10L);
        callableStatement.setShort(6, (short) 12);
        callableStatement.setInt(7, 1);
        callableStatement.setString(8, "test");
        callableStatement.setBoolean(9, true);
        callableStatement.setLong(10, 100L);
        callableStatement.setDouble(11, 12.0);
        callableStatement.setFloat(12, 12.0f);
        callableStatement.setByte(13, (byte) 1);
        callableStatement.setBytes(14, bytesParam);
        callableStatement.setDate(15, new Date(System.currentTimeMillis()));
        callableStatement.setNull(16, 1);
        callableStatement.setNull(17, 1, "test");
        callableStatement.setBigDecimal(18, new BigDecimal(10000));
        callableStatement.setBlob(19, inputStream);
        callableStatement.setBlob(20, inputStream, 1000000L);
        callableStatement.setClob(21, clob);
        callableStatement.setClob(22, reader);
        callableStatement.setClob(23, reader, 100L);
        callableStatement.setNString(24, "test");
        callableStatement.setNCharacterStream(25, reader);
        callableStatement.setNCharacterStream(26, reader, 1);
        callableStatement.setNClob(27, nClob);
        callableStatement.setNClob(28, reader, 1);
        callableStatement.setObject(29, new Object());
        callableStatement.setObject(30, new Object(), 1);
        callableStatement.setObject(31, new Object(), 1, 1);
        callableStatement.setRef(32, ref);
        callableStatement.setRowId(33, rowId);
        callableStatement.setSQLXML(34, sqlxml);
        callableStatement.setTime(35, new Time(System.currentTimeMillis()));
        callableStatement.setTimestamp(36, new Timestamp(System.currentTimeMillis()));
        callableStatement.setTimestamp(37, new Timestamp(System.currentTimeMillis()), Calendar.getInstance());
        callableStatement.setURL(38, new URL("http", "127.0.0.1", "test"));
        callableStatement.setBinaryStream(39, inputStream);
        callableStatement.setBinaryStream(40, inputStream, 1);
        callableStatement.setBinaryStream(41, inputStream, 1L);
        callableStatement.setNClob(42, reader);
        callableStatement.setTime(43, new Time(System.currentTimeMillis()), Calendar.getInstance());
        callableStatement.setArray(45, array);
        callableStatement.setBlob(46, blob);
        callableStatement.setDate(47, new Date(System.currentTimeMillis()), Calendar.getInstance());


        callableStatement.getCharacterStream(4);
        callableStatement.getCharacterStream("d");
        callableStatement.getShort(6);
        callableStatement.getShort("g");
        callableStatement.getInt(7);
        callableStatement.getInt("h");
        callableStatement.getString(8);
        callableStatement.getString("i");
        callableStatement.getBoolean(9);
        callableStatement.getBoolean("j");
        callableStatement.getLong(10);
        callableStatement.getLong("k");
        callableStatement.getDouble(11);
        callableStatement.getDouble("l");
        callableStatement.getFloat(12);
        callableStatement.getFloat("m");
        callableStatement.getByte(13);
        callableStatement.getByte("n");
        callableStatement.getBytes(14);
        callableStatement.getBytes("o");
        callableStatement.getDate(15);
        callableStatement.getDate("p");
        callableStatement.getBigDecimal(18);
        callableStatement.getBigDecimal("s");
        callableStatement.getBlob(19);
        callableStatement.getBlob("t");
        callableStatement.getClob(21);
        callableStatement.getClob(21);
        callableStatement.getClob("u");
        callableStatement.getNString(24);
        callableStatement.getNString("y");
        callableStatement.getNCharacterStream(25);
        callableStatement.getNCharacterStream("z");
        callableStatement.getNClob(27);
        callableStatement.getNClob("a1");
        callableStatement.getRef(32);
        callableStatement.getRef("a2");
        callableStatement.getRowId(33);
        callableStatement.getRowId("a7");
        callableStatement.getSQLXML(34);
        callableStatement.getSQLXML("a8");
        callableStatement.getTime(35);
        callableStatement.getTime("a9");
        callableStatement.getTimestamp(36);
        callableStatement.getTimestamp("b1");
        callableStatement.getURL(38);
        callableStatement.getURL("b3");
        callableStatement.getArray(45);
        callableStatement.getArray("c4");
        callableStatement.getDate(15);
        callableStatement.getDate("p");
        callableStatement.getDate(15, Calendar.getInstance());
        callableStatement.getDate("p", Calendar.getInstance());
        callableStatement.getTime("a9");
        callableStatement.getTime("a9", Calendar.getInstance());
        callableStatement.getTime(43);
        callableStatement.getTime(43, Calendar.getInstance());
        callableStatement.getTimestamp("p", Calendar.getInstance());
        callableStatement.getTimestamp(36, Calendar.getInstance());
        callableStatement.getObject(29);
        callableStatement.getObject(29, new HashMap<String, Class<?>>());
        callableStatement.getObject("a4");
        callableStatement.getObject("a4", new HashMap<String, Class<?>>());
        callableStatement.getBigDecimal(18, 1);
        callableStatement.wasNull();

        callableStatement.setAsciiStream("a", inputStream);
        callableStatement.setAsciiStream("b", inputStream, 10);
        callableStatement.setAsciiStream("c", inputStream, 1000000L);
        callableStatement.setCharacterStream("d", reader);
        callableStatement.setCharacterStream("e", reader, 10);
        callableStatement.setCharacterStream("f", reader, 10L);
        callableStatement.setShort("g", (short) 12);
        callableStatement.setInt("h", 1);
        callableStatement.setString("i", "test");
        callableStatement.setBoolean("j", true);
        callableStatement.setLong("k", 100L);
        callableStatement.setDouble("l", 12.0);
        callableStatement.setFloat("m", 12.0f);
        callableStatement.setByte("n", (byte) 1);
        callableStatement.setBytes("o", bytesParam);
        callableStatement.setDate("p", new Date(System.currentTimeMillis()));
        callableStatement.setNull("q", 1);
        callableStatement.setNull("r", 1, "test");
        callableStatement.setBigDecimal("s", new BigDecimal(10000));
        callableStatement.setBlob("t", inputStream);
        callableStatement.setBlob("u", inputStream, 1000000L);
        callableStatement.setClob("v", clob);
        callableStatement.setClob("w", reader);
        callableStatement.setClob("x", reader, 100L);
        callableStatement.setNString("y", "test");
        callableStatement.setNCharacterStream("z", reader);
        callableStatement.setNCharacterStream("a1", reader, 1);
        callableStatement.setNClob("a2", nClob);
        callableStatement.setNClob("a3", reader, 1);
        callableStatement.setObject("a4", new Object());
        callableStatement.setObject("a5", new Object(), 1);
        callableStatement.setObject("a6", new Object(), 1, 1);
        callableStatement.setRowId("a7", rowId);
        callableStatement.setSQLXML("a8", sqlxml);
        callableStatement.setTime("a9", new Time(System.currentTimeMillis()));
        callableStatement.setTimestamp("b1", new Timestamp(System.currentTimeMillis()));
        callableStatement.setTimestamp("b2", new Timestamp(System.currentTimeMillis()), Calendar.getInstance());
        callableStatement.setURL("b3", new URL("http", "127.0.0.1", "test"));
        callableStatement.setBinaryStream("b4", inputStream);
        callableStatement.setBinaryStream("b5", inputStream, 1);
        callableStatement.setBinaryStream("b6", inputStream, 1L);
        callableStatement.setNClob("b7", reader);
        callableStatement.setTime("b8", new Time(System.currentTimeMillis()), Calendar.getInstance());
        callableStatement.setBlob("c1", blob);
        callableStatement.setDate("c2", new Date(System.currentTimeMillis()), Calendar.getInstance());

        callableStatement.registerOutParameter("c4", 1);
        callableStatement.registerOutParameter("c5", 1, 1);
        callableStatement.registerOutParameter("c6", 1, "test");
        callableStatement.registerOutParameter(48, 1);
        callableStatement.registerOutParameter(49, 1, 1);
        callableStatement.registerOutParameter(50, 1, "test");

        ResultSet resultSet = callableStatement.executeQuery();
        callableStatement.close();

        verify(mysqlCallableStatement, times(1)).clearParameters();
        verify(mysqlCallableStatement, times(1)).executeQuery();
        verify(mysqlCallableStatement, times(1)).close();
        verify(mysqlCallableStatement, times(1)).setAsciiStream(anyInt(), any(InputStream.class));
        verify(mysqlCallableStatement, times(1)).setAsciiStream(anyInt(), any(InputStream.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setAsciiStream(anyInt(), any(InputStream.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setCharacterStream(anyInt(), any(Reader.class));
        verify(mysqlCallableStatement, times(1)).setCharacterStream(anyInt(), any(Reader.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setCharacterStream(anyInt(), any(Reader.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setShort(anyInt(), anyShort());
        verify(mysqlCallableStatement, times(1)).setInt(anyInt(), anyInt());
        verify(mysqlCallableStatement, times(1)).setString(anyInt(), anyString());
        verify(mysqlCallableStatement, times(1)).setBoolean(anyInt(), anyBoolean());
        verify(mysqlCallableStatement, times(1)).setLong(anyInt(), anyLong());
        verify(mysqlCallableStatement, times(1)).setDouble(anyInt(), anyDouble());
        verify(mysqlCallableStatement, times(1)).setFloat(anyInt(), anyFloat());
        verify(mysqlCallableStatement, times(1)).setByte(anyInt(), anyByte());
        verify(mysqlCallableStatement, times(1)).setBytes(14, bytesParam);
        verify(mysqlCallableStatement, times(1)).setDate(anyInt(), any(Date.class));
        verify(mysqlCallableStatement, times(1)).setNull(anyInt(), anyInt());
        verify(mysqlCallableStatement, times(1)).setNull(anyInt(), anyInt(), anyString());
        verify(mysqlCallableStatement, times(1)).setBigDecimal(anyInt(), any(BigDecimal.class));
        verify(mysqlCallableStatement, times(1)).setBlob(anyInt(), any(InputStream.class));
        verify(mysqlCallableStatement, times(1)).setBlob(anyInt(), any(InputStream.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setClob(anyInt(), any(Clob.class));
        verify(mysqlCallableStatement, times(1)).setClob(anyInt(), any(Reader.class));
        verify(mysqlCallableStatement, times(1)).setClob(anyInt(), any(Reader.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setNString(anyInt(), anyString());
        verify(mysqlCallableStatement, times(1)).setNCharacterStream(anyInt(), any(Reader.class));
        verify(mysqlCallableStatement, times(1)).setNCharacterStream(anyInt(), any(Reader.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setNClob(27, nClob);
        verify(mysqlCallableStatement, times(1)).setNClob(28, reader, 1);
        verify(mysqlCallableStatement, times(1)).setObject(anyInt(), Matchers.anyObject());
        verify(mysqlCallableStatement, times(1)).setObject(anyInt(), Matchers.anyObject(), anyInt());
        verify(mysqlCallableStatement, times(1)).setObject(anyInt(), Matchers.anyObject(), anyInt(), anyInt());
        verify(mysqlCallableStatement, times(1)).setRef(anyInt(), any(Ref.class));
        verify(mysqlCallableStatement, times(1)).setRowId(anyInt(), any(RowId.class));
        verify(mysqlCallableStatement, times(1)).setSQLXML(anyInt(), any(SQLXML.class));
        verify(mysqlCallableStatement, times(1)).setTime(anyInt(), any(Time.class));
        verify(mysqlCallableStatement, times(1)).setTimestamp(anyInt(), any(Timestamp.class));
        verify(mysqlCallableStatement, times(1)).setTimestamp(anyInt(), any(Timestamp.class), any(Calendar.class));
        verify(mysqlCallableStatement, times(1)).setURL(anyInt(), any(URL.class));
        verify(mysqlCallableStatement, times(1)).setBinaryStream(anyInt(), any(InputStream.class));
        verify(mysqlCallableStatement, times(1)).setBinaryStream(anyInt(), any(InputStream.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setBinaryStream(anyInt(), any(InputStream.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setNClob(42, reader);
        verify(mysqlCallableStatement, times(1)).setTime(anyInt(), any(Time.class), any(Calendar.class));
        verify(mysqlCallableStatement, times(1)).setTimestamp(anyInt(), any(Timestamp.class), any(Calendar.class));
        verify(mysqlCallableStatement, times(1)).setArray(anyInt(), any(Array.class));
        verify(mysqlCallableStatement, times(1)).setBlob(anyInt(), any(Blob.class));
        verify(mysqlCallableStatement, times(1)).setDate(anyInt(), any(Date.class), any(Calendar.class));


        verify(mysqlCallableStatement, times(1)).clearParameters();
        verify(mysqlCallableStatement, times(1)).executeQuery();
        verify(mysqlCallableStatement, times(1)).close();
        verify(mysqlCallableStatement, times(1)).setAsciiStream(anyString(), any(InputStream.class));
        verify(mysqlCallableStatement, times(1)).setAsciiStream(anyString(), any(InputStream.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setAsciiStream(anyString(), any(InputStream.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setCharacterStream(anyString(), any(Reader.class));
        verify(mysqlCallableStatement, times(1)).setCharacterStream(anyString(), any(Reader.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setCharacterStream(anyString(), any(Reader.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setShort(anyString(), anyShort());
        verify(mysqlCallableStatement, times(1)).setInt(anyString(), anyInt());
        verify(mysqlCallableStatement, times(1)).setString(anyString(), anyString());
        verify(mysqlCallableStatement, times(1)).setBoolean(anyString(), anyBoolean());
        verify(mysqlCallableStatement, times(1)).setLong(anyString(), anyLong());
        verify(mysqlCallableStatement, times(1)).setDouble(anyString(), anyDouble());
        verify(mysqlCallableStatement, times(1)).setFloat(anyString(), anyFloat());
        verify(mysqlCallableStatement, times(1)).setByte(anyString(), anyByte());
        verify(mysqlCallableStatement, times(1)).setBytes(14, bytesParam);
        verify(mysqlCallableStatement, times(1)).setDate(anyString(), any(Date.class));
        verify(mysqlCallableStatement, times(1)).setNull(anyString(), anyInt());
        verify(mysqlCallableStatement, times(1)).setNull(anyString(), anyInt(), anyString());
        verify(mysqlCallableStatement, times(1)).setBigDecimal(anyString(), any(BigDecimal.class));
        verify(mysqlCallableStatement, times(1)).setBlob(anyString(), any(InputStream.class));
        verify(mysqlCallableStatement, times(1)).setBlob(anyString(), any(InputStream.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setClob(anyString(), any(Clob.class));
        verify(mysqlCallableStatement, times(1)).setClob(anyString(), any(Reader.class));
        verify(mysqlCallableStatement, times(1)).setClob(anyString(), any(Reader.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setNString(anyString(), anyString());
        verify(mysqlCallableStatement, times(1)).setNCharacterStream(anyString(), any(Reader.class));
        verify(mysqlCallableStatement, times(1)).setNCharacterStream(anyString(), any(Reader.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setNClob(27, nClob);
        verify(mysqlCallableStatement, times(1)).setNClob(28, reader, 1);
        verify(mysqlCallableStatement, times(1)).setObject(anyString(), Matchers.anyObject());
        verify(mysqlCallableStatement, times(1)).setObject(anyString(), Matchers.anyObject(), anyInt());
        verify(mysqlCallableStatement, times(1)).setObject(anyString(), Matchers.anyObject(), anyInt(), anyInt());
        verify(mysqlCallableStatement, times(1)).setRowId(anyString(), any(RowId.class));
        verify(mysqlCallableStatement, times(1)).setSQLXML(anyString(), any(SQLXML.class));
        verify(mysqlCallableStatement, times(1)).setTime(anyString(), any(Time.class));
        verify(mysqlCallableStatement, times(1)).setTimestamp(anyString(), any(Timestamp.class));
        verify(mysqlCallableStatement, times(1)).setTimestamp(anyString(), any(Timestamp.class), any(Calendar.class));
        verify(mysqlCallableStatement, times(1)).setURL(anyString(), any(URL.class));
        verify(mysqlCallableStatement, times(1)).setBinaryStream(anyString(), any(InputStream.class));
        verify(mysqlCallableStatement, times(1)).setBinaryStream(anyString(), any(InputStream.class), anyInt());
        verify(mysqlCallableStatement, times(1)).setBinaryStream(anyString(), any(InputStream.class), anyLong());
        verify(mysqlCallableStatement, times(1)).setNClob(42, reader);
        verify(mysqlCallableStatement, times(1)).setTime(anyString(), any(Time.class), any(Calendar.class));
        verify(mysqlCallableStatement, times(1)).setTimestamp(anyString(), any(Timestamp.class), any(Calendar.class));
        verify(mysqlCallableStatement, times(1)).setBlob(anyString(), any(Blob.class));
        verify(mysqlCallableStatement, times(1)).setDate(anyString(), any(Date.class), any(Calendar.class));
    }

    @Test
    public void testCallableStatementConfig() throws SQLException {
        CallableStatement callableStatement = swConnection.prepareCall("INSERT INTO test VALUES( ? , ?)", 1, 1);
        callableStatement.setInt(1, 1);
        callableStatement.setString(2, "a");
        callableStatement.getUpdateCount();
        callableStatement.setFetchDirection(1);
        callableStatement.getFetchDirection();
        callableStatement.getResultSetConcurrency();
        callableStatement.getResultSetType();
        callableStatement.isClosed();
        callableStatement.setPoolable(false);
        callableStatement.isPoolable();
        callableStatement.getWarnings();
        callableStatement.clearWarnings();
        callableStatement.setCursorName("test");
        callableStatement.setMaxFieldSize(11);
        callableStatement.getMaxFieldSize();
        callableStatement.setMaxRows(10);
        callableStatement.getMaxRows();
        callableStatement.getParameterMetaData();
        callableStatement.setEscapeProcessing(true);
        callableStatement.setFetchSize(1);
        callableStatement.getFetchSize();
        callableStatement.setQueryTimeout(1);
        callableStatement.getQueryTimeout();
        Connection connection = callableStatement.getConnection();

        callableStatement.execute();

        callableStatement.getMoreResults();
        callableStatement.getMoreResults(1);
        callableStatement.getResultSetHoldability();
        callableStatement.getMetaData();
        callableStatement.getResultSet();

        callableStatement.close();
        verify(mysqlCallableStatement, times(1)).getUpdateCount();
        verify(mysqlCallableStatement, times(1)).getMoreResults();
        verify(mysqlCallableStatement, times(1)).setFetchDirection(anyInt());
        verify(mysqlCallableStatement, times(1)).getFetchDirection();
        verify(mysqlCallableStatement, times(1)).getResultSetType();
        verify(mysqlCallableStatement, times(1)).isClosed();
        verify(mysqlCallableStatement, times(1)).setPoolable(anyBoolean());
        verify(mysqlCallableStatement, times(1)).getWarnings();
        verify(mysqlCallableStatement, times(1)).clearWarnings();
        verify(mysqlCallableStatement, times(1)).setCursorName(anyString());
        verify(mysqlCallableStatement, times(1)).setMaxFieldSize(anyInt());
        verify(mysqlCallableStatement, times(1)).getMaxFieldSize();
        verify(mysqlCallableStatement, times(1)).setMaxRows(anyInt());
        verify(mysqlCallableStatement, times(1)).getMaxRows();
        verify(mysqlCallableStatement, times(1)).setEscapeProcessing(anyBoolean());
        verify(mysqlCallableStatement, times(1)).getResultSetConcurrency();
        verify(mysqlCallableStatement, times(1)).getResultSetConcurrency();
        verify(mysqlCallableStatement, times(1)).getResultSetType();
        verify(mysqlCallableStatement, times(1)).getMetaData();
        verify(mysqlCallableStatement, times(1)).getParameterMetaData();
        verify(mysqlCallableStatement, times(1)).getMoreResults(anyInt());
        verify(mysqlCallableStatement, times(1)).setFetchSize(anyInt());
        verify(mysqlCallableStatement, times(1)).getFetchSize();
        verify(mysqlCallableStatement, times(1)).getQueryTimeout();
        verify(mysqlCallableStatement, times(1)).setQueryTimeout(anyInt());
        verify(mysqlCallableStatement, times(1)).getResultSet();
        assertThat(connection, CoreMatchers.<Connection>is(swConnection));
    }

    @Test
    public void testExecuteQuery() throws SQLException {
        CallableStatement callableStatement = swConnection.prepareCall("SELECT * FROM test", 1, 1, 1);
        ResultSet resultSet = callableStatement.executeQuery();

        callableStatement.close();

        verify(mysqlCallableStatement, times(1)).executeQuery();
        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeQuery", "SELECT * FROM test");
            }
        });
    }

    @Test
    public void testQuerySqlWithSql() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("SELECT * FROM test", 1, 1);
        ResultSet resultSet = preparedStatement.executeQuery("SELECT * FROM test");

        preparedStatement.getGeneratedKeys();
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).executeQuery(anyString());
        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeQuery", "SELECT * FROM test");
            }
        });
    }

    @Test
    public void testInsertWithAutoGeneratedKey() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("INSERT INTO test VALUES(?)");
        boolean insertCount = preparedStatement.execute("INSERT INTO test VALUES(1)", 1);
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).execute(anyString(), anyInt());
        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/execute", "INSERT INTO test VALUES(1)");
            }
        });
    }

    @Test
    public void testInsertWithIntColumnIndexes() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("INSERT INTO test VALUES(?)");
        boolean insertCount = preparedStatement.execute("INSERT INTO test VALUES(1)", new int[]{1, 2});
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/execute", "INSERT INTO test VALUES(1)");
            }
        });
    }

    @Test
    public void testInsertWithStringColumnIndexes() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("INSERT INTO test VALUES(?)");
        boolean insertCount = preparedStatement.execute("INSERT INTO test VALUES(1)", new String[]{"1", "2"});
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/execute", "INSERT INTO test VALUES(1)");
            }
        });
    }

    @Test
    public void testExecute() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("UPDATE test SET  a = ?");
        preparedStatement.setString(1, "a");
        boolean updateCount = preparedStatement.execute("UPDATE test SET  a = 1");
        preparedStatement.cancel();
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).execute(anyString());
        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/execute", "UPDATE test SET  a = 1");
            }
        });
    }

    @Test
    public void testExecuteUpdate() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("UPDATE test SET  a = ?");
        preparedStatement.setString(1, "a");
        int updateCount = preparedStatement.executeUpdate();
        preparedStatement.cancel();
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).executeUpdate();
        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeUpdate", "UPDATE test SET  a = ?");
            }
        });
    }

    @Test
    public void testUpdateSql() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("UPDATE test SET  a = ?");

        int updateCount = preparedStatement.executeUpdate("UPDATE test SET  a = 1");
        preparedStatement.cancel();
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).executeUpdate(anyString());
        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeUpdate", "UPDATE test SET  a = 1");
            }
        });
    }

    @Test
    public void testUpdateWithAutoGeneratedKey() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("UPDATE test SET  a = ?");

        int updateCount = preparedStatement.executeUpdate("UPDATE test SET  a = 1", 1);
        preparedStatement.cancel();
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeUpdate", "UPDATE test SET  a = 1");
            }
        });
    }

    @Test
    public void testUpdateWithIntColumnIndexes() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("UPDATE test SET  a = ?");

        int updateCount = preparedStatement.executeUpdate("UPDATE test SET  a = 1", new int[]{1});
        preparedStatement.cancel();
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeUpdate", "UPDATE test SET  a = 1");
            }
        });
    }

    @Test
    public void testUpdateWithStringColumnIndexes() throws SQLException {
        CallableStatement preparedStatement = swConnection.prepareCall("UPDATE test SET  a = ?");

        int updateCount = preparedStatement.executeUpdate("UPDATE test SET  a = 1", new String[]{"1"});
        preparedStatement.cancel();
        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).close();
        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeUpdate", "UPDATE test SET  a = 1");
            }
        });
    }


    @Test
    public void testBatch() throws SQLException, MalformedURLException {
        CallableStatement preparedStatement = multiHostConnection.prepareCall("UPDATE test SET a = ? WHERE b = ?");
        preparedStatement.setShort(1, (short) 12);
        preparedStatement.setTime(2, new Time(System.currentTimeMillis()));
        preparedStatement.addBatch();
        int[] resultSet = preparedStatement.executeBatch();
        preparedStatement.clearBatch();

        verify(mysqlCallableStatement, times(1)).executeBatch();
        verify(mysqlCallableStatement, times(1)).addBatch();
        verify(mysqlCallableStatement, times(1)).clearBatch();

        mockTracerContextListener.assertSize(1);
        mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
            @Override
            public void call(TraceSegment traceSegment) {
                assertThat(traceSegment.getSpans().size(), is(1));
                Span span = traceSegment.getSpans().get(0);
                assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeBatch", "");
            }
        });
    }

    @Test
    public void testQueryWithMultiHost() throws SQLException {
        CallableStatement preparedStatement = multiHostConnection.prepareCall("SELECT * FROM test WHERE a = ? or b = ? or c=? or d = ?", 1, 1);
        preparedStatement.setAsciiStream(1, inputStream);
        preparedStatement.setAsciiStream(2, inputStream, 10);
        preparedStatement.setAsciiStream(3, inputStream, 1000000L);
        preparedStatement.setCharacterStream(4, reader);
        ResultSet resultSet = preparedStatement.executeQuery();

        preparedStatement.close();

        verify(mysqlCallableStatement, times(1)).executeQuery();
        verify(mysqlCallableStatement, times(1)).close();
    }


    @Test(expected = SQLException.class)
    public void testMultiHostWithException() throws SQLException {
        when(mysqlCallableStatement.executeQuery()).thenThrow(new SQLException());
        try {
            CallableStatement preparedStatement = multiHostConnection.prepareCall("SELECT * FROM test WHERE a = ? or b = ? or c=? or d = ? or e=?");
            preparedStatement.setBigDecimal(1, new BigDecimal(10000));
            preparedStatement.setBlob(2, inputStream);
            preparedStatement.setBlob(3, inputStream, 1000000L);
            preparedStatement.setByte(3, (byte) 1);
            preparedStatement.setBytes(4, bytesParam);
            preparedStatement.setLong(5, 100L);

            ResultSet resultSet = preparedStatement.executeQuery();

            preparedStatement.close();
        } finally {
            verify(mysqlCallableStatement, times(1)).executeQuery();
            verify(mysqlCallableStatement, times(0)).close();
            verify(mysqlCallableStatement, times(1)).setBigDecimal(anyInt(), any(BigDecimal.class));
            verify(mysqlCallableStatement, times(1)).setBlob(anyInt(), any(InputStream.class));
            verify(mysqlCallableStatement, times(1)).setBlob(anyInt(), any(InputStream.class), anyLong());
            verify(mysqlCallableStatement, times(1)).setByte(anyInt(), anyByte());
            mockTracerContextListener.assertSize(1);
            mockTracerContextListener.assertTraceSegment(0, new SegmentAssert() {
                @Override
                public void call(TraceSegment traceSegment) {
                    assertThat(traceSegment.getSpans().size(), is(1));
                    Span span = traceSegment.getSpans().get(0);
                    assertDBSpan(span, "Mysql/JDBI/CallableStatement/executeQuery", "SELECT * FROM test WHERE a = ? or b = ? or c=? or d = ? or e=?");
                    assertThat(span.getLogs().size(), is(1));
                    assertDBSpanLog(span.getLogs().get(0));
                }
            });
        }
    }

    @After
    public void tearDown() throws Exception {
        TracerContext.ListenerManager.remove(mockTracerContextListener);
    }

}