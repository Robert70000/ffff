package org.owntracks.android.messages;
import androidx.annotation.NonNull;
import androidx.databinding.BaseObservable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.owntracks.android.support.Preferences;
import org.owntracks.android.support.interfaces.IncomingMessageProcessor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "_type", defaultImpl = MessageUnknown.class)

@JsonSubTypes({
        @JsonSubTypes.Type(value=MessageLocation.class, name=MessageLocation.TYPE),
        @JsonSubTypes.Type(value=MessageTransition.class, name=MessageTransition.TYPE),
        @JsonSubTypes.Type(value=MessageEvent.class, name=MessageEvent.TYPE),
        @JsonSubTypes.Type(value=MessageCard.class, name=MessageCard.TYPE),
        @JsonSubTypes.Type(value=MessageCmd.class, name=MessageCmd.TYPE),
        @JsonSubTypes.Type(value=MessageConfiguration.class, name=MessageConfiguration.TYPE),
        @JsonSubTypes.Type(value=MessageEncrypted.class, name=MessageEncrypted.TYPE),
        @JsonSubTypes.Type(value=MessageWaypoint.class, name=MessageWaypoint.TYPE),
        @JsonSubTypes.Type(value=MessageWaypoints.class, name=MessageWaypoints.TYPE),
        @JsonSubTypes.Type(value=MessageLwt.class, name=MessageLwt.TYPE),
})
@JsonPropertyOrder(alphabetic=true)
public abstract class MessageBase extends BaseObservable  {
        static final String TYPE = "base";

        @JsonIgnore
        String _topic;
        @JsonIgnore
        private String _topic_base;

        @JsonIgnore
        private int modeId;

        @JsonIgnore
        private boolean incoming = false;

        @JsonIgnore
        public long getMessageId() {
                return _messageId;
        }

        @JsonIgnore
        private final Long _messageId = System.currentTimeMillis();

        @JsonIgnore
        private int _mqtt_qos;

        @JsonIgnore
        private boolean _mqtt_retained;
        private String tid;

        @JsonIgnore
        public boolean getRetained() {
                return _mqtt_retained;
        }

        @JsonIgnore
        public void setRetained(boolean _mqtt_retained) {
                this._mqtt_retained = _mqtt_retained;
        }
        @JsonIgnore
        public int getQos() {
                return _mqtt_qos;
        }

        @JsonIgnore
        public void setQos(int _mqtt_qos) {
                this._mqtt_qos = _mqtt_qos;
        }

        @JsonIgnore
        @NonNull
        public String getContactKey() {
                if(_topic_base != null)
                        return _topic_base;
                if(tid != null)
                        return tid;
                return
                        "NOKEY";
        }

        public String getTopic() {
                return _topic;
        }

        @JsonIgnore
        public void setTopic(String topic) {
                this._topic = topic;
                this._topic_base = getBaseTopic(topic); // Normalized topic for all message types
        }

        @JsonIgnore
        public void setIncoming() {
                this.incoming = true;
        }

        @JsonIgnore
        public abstract void processIncomingMessage(IncomingMessageProcessor handler);

        @JsonIgnore
        protected abstract String getBaseTopicSuffix();

        // Called after deserialization to check if all required attributes are set or not.
        // The message is discarded if false is returned.
        @JsonIgnore
        public boolean isValidMessage() {
                return true;
        }

        @JsonIgnore
        public boolean isIncoming() {
                return this.incoming;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        public String getTid() {
                return tid;
        }

        @JsonIgnore
        public boolean hasTid() {
                return getTid() != null;
        }

        @JsonInclude(JsonInclude.Include.NON_NULL)
        void setTid(String tid) {
                this.tid = tid;
        }

        @JsonIgnore
        private String getBaseTopic(String topic){

                if (this.getBaseTopicSuffix() != null && topic.endsWith(this.getBaseTopicSuffix())) {
                        return topic.substring(0, (topic.length() - this.getBaseTopicSuffix().length()));
                } else {
                        return topic;
                }
        }

        @JsonIgnore
        public void setModeId(int modeId) {
                this.modeId = modeId;
        }

        @JsonIgnore
        public int getModeId() {
                return this.modeId;
        }


        @JsonIgnore
        @Override
        @NonNull
        public String toString() {
                return String.format("%s id=%s",this.getClass().getName(), this.getMessageId());
        }

        public abstract void addMqttPreferences(Preferences preferences);
}
