package com.a.eye.skywalking.collector.worker.application.persistence;


import akka.actor.ActorRef;
import com.a.eye.skywalking.collector.actor.AbstractAsyncMemberProvider;
import com.a.eye.skywalking.collector.queue.MessageHolder;
import com.a.eye.skywalking.collector.worker.RecordPersistenceMember;
import com.a.eye.skywalking.collector.worker.WorkerConfig;
import com.a.eye.skywalking.collector.worker.receiver.TraceSegmentReceiver;
import com.a.eye.skywalking.collector.worker.storage.RecordData;
import com.a.eye.skywalking.collector.worker.tools.DateTools;
import com.a.eye.skywalking.trace.Span;
import com.a.eye.skywalking.trace.TraceSegment;
import com.a.eye.skywalking.trace.TraceSegmentRef;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.lmax.disruptor.RingBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * @author pengys5
 */
public class TraceSegmentRecordPersistence extends RecordPersistenceMember {

    private Logger logger = LogManager.getFormatterLogger(TraceSegmentRecordPersistence.class);

    @Override
    public String esIndex() {
        return "application_record";
    }

    @Override
    public String esType() {
        return "trace_segment";
    }

    public TraceSegmentRecordPersistence(RingBuffer<MessageHolder> ringBuffer, ActorRef actorRef) {
        super(ringBuffer, actorRef);
    }

    @Override
    public void analyse(Object message) throws Exception {
        if (message instanceof TraceSegmentReceiver.TraceSegmentTimeSlice) {
            TraceSegmentReceiver.TraceSegmentTimeSlice traceSegment = (TraceSegmentReceiver.TraceSegmentTimeSlice) message;
            JsonObject jsonObject = parseTraceSegment(traceSegment.getTraceSegment(), traceSegment.getMinute());

            RecordData recordData = new RecordData(traceSegment.getTraceSegment().getTraceSegmentId());
            recordData.setRecord(jsonObject);
            super.analyse(recordData);
        }
    }

    public static class Factory extends AbstractAsyncMemberProvider<TraceSegmentRecordPersistence> {
        public static Factory INSTANCE = new Factory();

        @Override
        public int queueSize() {
            return WorkerConfig.Queue.TraceSegmentRecordAnalysis.Size;
        }

        @Override
        public Class memberClass() {
            return TraceSegmentRecordPersistence.class;
        }
    }

    private JsonObject parseTraceSegment(TraceSegment traceSegment, long minute) {
        JsonObject traceJsonObj = new JsonObject();
        traceJsonObj.addProperty("segmentId", traceSegment.getTraceSegmentId());
        traceJsonObj.addProperty(DateTools.Time_Slice_Column_Name, minute);
        traceJsonObj.addProperty("startTime", traceSegment.getStartTime());
        traceJsonObj.addProperty("endTime", traceSegment.getEndTime());
        traceJsonObj.addProperty("appCode", traceSegment.getApplicationCode());

        if (traceSegment.getPrimaryRef() != null) {
            JsonObject primaryRefJsonObj = parsePrimaryRef(traceSegment.getPrimaryRef());
            traceJsonObj.add("primaryRef", primaryRefJsonObj);
        }

        if (traceSegment.getRefs() != null) {
            JsonArray refsJsonArray = parseRefs(traceSegment.getRefs());
            traceJsonObj.add("refs", refsJsonArray);
        }

        JsonArray spanJsonArray = new JsonArray();
        for (Span span : traceSegment.getSpans()) {
            JsonObject spanJsonObj = parseSpan(span);
            spanJsonArray.add(spanJsonObj);
        }
        traceJsonObj.add("spans", spanJsonArray);

        return traceJsonObj;
    }

    private JsonObject parsePrimaryRef(TraceSegmentRef primaryRef) {
        JsonObject primaryRefJsonObj = new JsonObject();
        primaryRefJsonObj.addProperty("appCode", primaryRef.getApplicationCode());
        primaryRefJsonObj.addProperty("spanId", primaryRef.getSpanId());
        primaryRefJsonObj.addProperty("peerHost", primaryRef.getPeerHost());
        primaryRefJsonObj.addProperty("segmentId", primaryRef.getTraceSegmentId());
        return primaryRefJsonObj;
    }

    private JsonArray parseRefs(List<TraceSegmentRef> refs) {
        JsonArray refsJsonArray = new JsonArray();
        for (TraceSegmentRef ref : refs) {
            JsonObject refJsonObj = new JsonObject();
            refJsonObj.addProperty("spanId", ref.getSpanId());
            refJsonObj.addProperty("appCode", ref.getApplicationCode());
            refJsonObj.addProperty("segmentId", ref.getTraceSegmentId());
            refJsonObj.addProperty("peerHost", ref.getPeerHost());
            refsJsonArray.add(refJsonObj);
        }
        return refsJsonArray;
    }

    private JsonObject parseSpan(Span span) {
        JsonObject spanJsonObj = new JsonObject();
        spanJsonObj.addProperty("spanId", span.getSpanId());
        spanJsonObj.addProperty("parentSpanId", span.getParentSpanId());
        spanJsonObj.addProperty("startTime", span.getStartTime());
        spanJsonObj.addProperty("endTime", span.getEndTime());
        spanJsonObj.addProperty("operationName", span.getOperationName());

        JsonObject tagsJsonObj = parseSpanTag(span.getTags());
        spanJsonObj.add("tags", tagsJsonObj);
        return spanJsonObj;
    }

    private JsonObject parseSpanTag(Map<String, Object> tags) {
        JsonObject tagsJsonObj = new JsonObject();

        for (Map.Entry<String, Object> entry : tags.entrySet()) {
            String key = entry.getKey();
            String value = String.valueOf(entry.getValue());
            tagsJsonObj.addProperty(key, value);
        }
        return tagsJsonObj;
    }
}
