package com.kenzie.capstone.service.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.time.LocalDateTime;
import java.util.Objects;


    @DynamoDBTable(tableName = "NotificationTable")
    public class NotificationRecord {

        private String id;

        private String data;


        @DynamoDBHashKey(attributeName = "id")
        public String getId() {
            return id;
        }

        @DynamoDBAttribute(attributeName = "data")
        public String getData() {
            return data;
        }



        public void setId(String id) {
            this.id = id;
        }

        public void setData(String data) {
            this.data = data;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            com.kenzie.capstone.service.model.NotificationRecord that = (com.kenzie.capstone.service.model.NotificationRecord) o;
            return Objects.equals(id, that.id) && Objects.equals(data, that.data);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, data);
        }
    }


