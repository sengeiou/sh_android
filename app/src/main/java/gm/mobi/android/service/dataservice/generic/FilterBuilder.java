package gm.mobi.android.service.dataservice.generic;

import gm.mobi.android.db.GMContract.SyncColumns;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class FilterBuilder {

    // Nexus names
    public static String NEXUS_AND = "and";
    public static String NEXUS_OR = "or";
    // Comparator
    public static String COMPARATOR_NOT_EQUAL = "ne";
    public static String COMPARATOR_EQUAL = "eq";
    public static String COMPARATOR_GREAT_THAN = "gt";
    public static String COMPARATOR_GREAT_EQUAL_THAN = "ge";
    public static String COMPARATOR_LESS_THAN = "lt";
    public static String COMPARATOR_LESS_EQUAL_THAN = "le";
    public static String COMPARATOR_STARTS_WITH = "st";
    public static String COMPARATOR_NOT_STARTS_WITH = "nst";
    public static String COMPARATOR_CONTAINS = "ct";
    public static String COMPARATOR_NOT_CONTAINS = "nct";
    public static String COMPARATOR_ENDS_WITH = "en";
    public static String COMPARATOR_NOT_ENDS_WITH = "nen";

    public static ItemField<AndItem> and(String field) {
        AndItem andItem = new AndItem();
        return andItem.and(field);
    }

    public static AndItem and(NexusItem nexusItem) {
        AndItem andItem = new AndItem();
        return andItem.and(nexusItem);
    }

    public static AndItem and(OrItem... nexusItems) {
        AndItem andItem = new AndItem();
        for (NexusItem nexusItem : nexusItems) {
            andItem.and(nexusItem);
        }
        return andItem;
    }

    public static ItemField<OrItem> or(String field) {
        OrItem orItem = new OrItem();
        return orItem.or(field);
    }

    public static OrItem or(NexusItem nexusItem) {
        OrItem orItem = new OrItem();
        return orItem.or(nexusItem);
    }

    /**
     * Utility method to provide common modified and delete dates filter.
     * Creates an OrItem with modified or modified date greater than the date provided.
     *
     * @param lastModifiedDate Timestamp
     * @return OrItem for attaching to a builder.
     */
    public static OrItem orModifiedOrDeletedAfter(Long lastModifiedDate) {
        return or(SyncColumns.CSYS_DELETED).greaterThan(lastModifiedDate)
                .or(SyncColumns.CSYS_MODIFIED).greaterThan(lastModifiedDate);
    }

    /**
     * Filter data field. Represents the field from the service which is going to be compared with
     * a value.
     * <p/>
     * Use one of its methods to define the comparison criteria.
     *
     * @param <T> Type of nexus item to return in the comparator methods, to allow further catenation.
     */
    public static class ItemField<T extends NexusItem> {
        private T nexusItemReference;
        private String fieldName;

        public ItemField(T nexusItemReference, String fieldName) {
            this.nexusItemReference = nexusItemReference;
            this.fieldName = fieldName;
        }

        public T isNotEqualTo(Object value) {
            createAndAddItem(COMPARATOR_NOT_EQUAL, value);
            return nexusItemReference;
        }

        public T isIn(Collection<?> values) {
            for (Object value : values) {
                createAndAddItem(COMPARATOR_EQUAL, value);
            }
            return nexusItemReference;
        }

        public T isEqualTo(Object value) {
            createAndAddItem(COMPARATOR_EQUAL, value);
            return nexusItemReference;
        }

        public T greaterThan(Object value) {
            createAndAddItem(COMPARATOR_GREAT_THAN, value);
            return nexusItemReference;
        }

        public T greaterOrEqualThan(Object value) {
            createAndAddItem(COMPARATOR_GREAT_EQUAL_THAN, value);
            return nexusItemReference;
        }

        public T lessThan(Object value) {
            createAndAddItem(COMPARATOR_LESS_THAN, value);
            return nexusItemReference;
        }

        public T lessOrEqualThan(Object value) {
            createAndAddItem(COMPARATOR_LESS_EQUAL_THAN, value);
            return nexusItemReference;
        }

        public T startsWith(String textValue) {
            createAndAddItem(COMPARATOR_STARTS_WITH, textValue);
            return nexusItemReference;
        }

        public T notStartsWith(String textValue) {
            createAndAddItem(COMPARATOR_NOT_STARTS_WITH, textValue);
            return nexusItemReference;
        }

        public T contains(String textValue) {
            createAndAddItem(COMPARATOR_CONTAINS, textValue);
            return nexusItemReference;
        }

        public T notContains(String textValue) {
            createAndAddItem(COMPARATOR_NOT_CONTAINS, textValue);
            return nexusItemReference;
        }

        public T endsWith(String textValue) {
            createAndAddItem(COMPARATOR_ENDS_WITH, textValue);
            return nexusItemReference;
        }

        public T notEndsWith(String textValue) {
            createAndAddItem(COMPARATOR_NOT_ENDS_WITH, textValue);
            return nexusItemReference;
        }


        private void createAndAddItem(String comparator, Object value) {
            FilterItemDto item = new FilterItemDto(comparator, fieldName, value);
            nexusItemReference.addItem(item);
        }


        @Override
        @Deprecated
        public boolean equals(Object o) {
            return super.equals(o);
        }

    }


    public static class OrItem extends NexusItem {
        protected OrItem() {
            super(NEXUS_OR);
        }

        public ItemField<OrItem> or(String field) {
            return new ItemField<>(this, field);
        }

        public OrItem or(NexusItem filterItem) {
            addFilter(filterItem);
            return this;
        }
    }


    public static class AndItem extends NexusItem {
        protected AndItem() {
            super(NEXUS_AND);
        }

        public ItemField<AndItem> and(String field) {
            return new ItemField<>(this, field);
        }

        public AndItem and(NexusItem filterItem) {
            addFilter(filterItem);
            return this;
        }
    }

    private static class NexusItem {
        String nexus;
        List<FilterItemDto> items = new ArrayList<>();
        List<NexusItem> moreFilters = new ArrayList<>();

        protected NexusItem(String nexus, FilterItemDto[] items) {
            this.nexus = nexus;
            this.items.addAll(Arrays.asList(items));
        }

        protected NexusItem(String nexus) {
            this.nexus = nexus;
        }

        protected void addItem(FilterItemDto itemDto) {
            items.add(itemDto);
        }

        protected void addFilter(NexusItem additionalNexusItem) {
            moreFilters.add(additionalNexusItem);
        }

        public FilterDto build() {
            FilterItemDto[] filterItems = items.toArray(new FilterItemDto[items.size()]);
            FilterDto[] filters = new FilterDto[moreFilters.size()];
            for (int i = 0; i < moreFilters.size(); i++) {
                filters[i] = moreFilters.get(i).build();
            }
            return new FilterDto(nexus, filterItems, filters);
        }

    }
}
