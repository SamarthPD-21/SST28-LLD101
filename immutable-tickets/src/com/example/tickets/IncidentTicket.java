package com.example.tickets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * INTENTION: A ticket should be an immutable record-like object.
 *
 * CURRENT STATE (BROKEN ON PURPOSE):
 * - mutable fields
 * - multiple constructors
 * - public setters
 * - tags list can be modified from outside
 * - validation is scattered elsewhere
 *
 * TODO (student): refactor to immutable + Builder.
 */
public class IncidentTicket {

    private final String id;
    private final String reporterEmail;
    private final String title;

    private final String description;
    private final String priority;       // LOW, MEDIUM, HIGH, CRITICAL
    private final List<String> tags;
    private final String assigneeEmail;
    private final boolean customerVisible;
    private final Integer slaMinutes;    // optional
    private final String source;         // e.g. "CLI", "WEBHOOK", "EMAIL"

    private IncidentTicket(Builder builder) {
        this.id = validateRequired("id", builder.id);
        this.reporterEmail = validateEmail("reporterEmail", builder.reporterEmail);
        this.title = validateRequired("title", builder.title);

        this.description = trimToNull(builder.description);
        this.priority = validatePriority(builder.priority == null ? "MEDIUM" : builder.priority);
        this.tags = Collections.unmodifiableList(new ArrayList<>(builder.tags));
        this.assigneeEmail = validateOptionalEmail("assigneeEmail", builder.assigneeEmail);
        this.customerVisible = builder.customerVisible;
        this.slaMinutes = validateSla(builder.slaMinutes);
        this.source = validateRequired("source", builder.source == null ? "CLI" : builder.source);
    }

    public static Builder builder(String id, String reporterEmail, String title) {
        return new Builder(id, reporterEmail, title);
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    // Getters
    public String getId() { return id; }
    public String getReporterEmail() { return reporterEmail; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }
    public List<String> getTags() { return tags; }
    public String getAssigneeEmail() { return assigneeEmail; }
    public boolean isCustomerVisible() { return customerVisible; }
    public Integer getSlaMinutes() { return slaMinutes; }
    public String getSource() { return source; }

    public static final class Builder {
        private String id;
        private String reporterEmail;
        private String title;

        private String description;
        private String priority = "MEDIUM";
        private List<String> tags = new ArrayList<>();
        private String assigneeEmail;
        private boolean customerVisible;
        private Integer slaMinutes;
        private String source = "CLI";

        private Builder(String id, String reporterEmail, String title) {
            this.id = id;
            this.reporterEmail = reporterEmail;
            this.title = title;
        }

        private Builder(IncidentTicket ticket) {
            this.id = ticket.id;
            this.reporterEmail = ticket.reporterEmail;
            this.title = ticket.title;
            this.description = ticket.description;
            this.priority = ticket.priority;
            this.tags = new ArrayList<>(ticket.tags);
            this.assigneeEmail = ticket.assigneeEmail;
            this.customerVisible = ticket.customerVisible;
            this.slaMinutes = ticket.slaMinutes;
            this.source = ticket.source;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder priority(String priority) {
            this.priority = priority;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags == null ? new ArrayList<>() : new ArrayList<>(tags);
            return this;
        }

        public Builder addTag(String tag) {
            if (tag == null || tag.trim().isEmpty()) {
                throw new IllegalArgumentException("tag required");
            }
            this.tags.add(tag.trim());
            return this;
        }

        public Builder assigneeEmail(String assigneeEmail) {
            this.assigneeEmail = assigneeEmail;
            return this;
        }

        public Builder customerVisible(boolean customerVisible) {
            this.customerVisible = customerVisible;
            return this;
        }

        public Builder slaMinutes(Integer slaMinutes) {
            this.slaMinutes = slaMinutes;
            return this;
        }

        public Builder source(String source) {
            this.source = source;
            return this;
        }

        public IncidentTicket build() {
            return new IncidentTicket(this);
        }
    }

    private static String validateRequired(String field, String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            throw new IllegalArgumentException(field + " required");
        }
        return normalized;
    }

    private static String validateEmail(String field, String value) {
        String normalized = validateRequired(field, value);
        if (!normalized.contains("@")) {
            throw new IllegalArgumentException(field + " invalid");
        }
        return normalized;
    }

    private static String validateOptionalEmail(String field, String value) {
        String normalized = trimToNull(value);
        if (normalized == null) {
            return null;
        }
        if (!normalized.contains("@")) {
            throw new IllegalArgumentException(field + " invalid");
        }
        return normalized;
    }

    private static String validatePriority(String value) {
        String normalized = validateRequired("priority", value).toUpperCase();
        if (!Objects.equals(normalized, "LOW")
                && !Objects.equals(normalized, "MEDIUM")
                && !Objects.equals(normalized, "HIGH")
                && !Objects.equals(normalized, "CRITICAL")) {
            throw new IllegalArgumentException("priority invalid");
        }
        return normalized;
    }

    private static Integer validateSla(Integer slaMinutes) {
        if (slaMinutes == null) {
            return null;
        }
        if (slaMinutes <= 0) {
            throw new IllegalArgumentException("slaMinutes must be > 0");
        }
        return slaMinutes;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    @Override
    public String toString() {
        return "IncidentTicket{" +
                "id='" + id + '\'' +
                ", reporterEmail='" + reporterEmail + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", priority='" + priority + '\'' +
                ", tags=" + tags +
                ", assigneeEmail='" + assigneeEmail + '\'' +
                ", customerVisible=" + customerVisible +
                ", slaMinutes=" + slaMinutes +
                ", source='" + source + '\'' +
                '}';
    }
}
