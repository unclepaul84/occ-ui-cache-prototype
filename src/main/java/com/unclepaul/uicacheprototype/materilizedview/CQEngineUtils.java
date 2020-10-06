package com.unclepaul.uicacheprototype.materilizedview;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.attribute.ReflectiveAttribute;
import com.googlecode.cqengine.index.navigable.NavigableIndex;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CQEngineUtils {
    /**
     * Generates attributes dynamically for the fields declared in the given POJO class.
     * <p/>
     * Implementation is currently limited to generating attributes for Comparable fields (String, Integer etc.).
     *
     * @param pojoClass A POJO class
     * @param <O> Type of the POJO class
     * @return Attributes for fields in the POJO
     */
    public static <O> Map<String, Attribute<O, Comparable>> generateAttributesForPojo(Class<O> pojoClass) {
        Map<String, Attribute<O, Comparable>> generatedAttributes = new LinkedHashMap<String, Attribute<O, Comparable>>();
        for (Field field : pojoClass.getDeclaredFields()) {
            if (Comparable.class.isAssignableFrom(field.getType())) {
                @SuppressWarnings({"unchecked"})
                Class<Comparable> fieldType = (Class<Comparable>) field.getType();
                generatedAttributes.put(field.getName(), ReflectiveAttribute.forField(pojoClass, fieldType, field.getName()));
            }
        }
        return generatedAttributes;
    }

    public static <O> Iterable<Attribute<O, Comparable>> generateAttributesListForPojo(Class<O> pojoClass) {
       List<Attribute<O, Comparable>> generatedAttributes = new ArrayList<Attribute<O, Comparable>>();
        for (Field field : pojoClass.getDeclaredFields()) {
           // if (Comparable.class.isAssignableFrom(field.getType())) {
                @SuppressWarnings({"unchecked"})
                Class<Comparable> fieldType = (Class<Comparable>) field.getType();
                generatedAttributes.add(ReflectiveAttribute.forField(pojoClass, fieldType, field.getName()));
            //}
        }
        return generatedAttributes;
    }

    /**
     * Creates an IndexedCollection and adds NavigableIndexes for the given attributes.
     *
     * @param attributes Attributes for which indexes should be added
     * @param <O> Type of objects stored in the collection
     * @return An IndexedCollection configured with indexes on the given attributes.
     */
    public static <O> IndexedCollection<O> newAutoIndexedCollection(Iterable<Attribute<O, Comparable>> attributes) {
        IndexedCollection<O> autoIndexedCollection = new ConcurrentIndexedCollection<O>();
        for (Attribute<O, ? extends Comparable> attribute : attributes) {
            // Add a NavigableIndex...
            @SuppressWarnings("unchecked")
            NavigableIndex<? extends Comparable, O> index = NavigableIndex.onAttribute(attribute);
            autoIndexedCollection.addIndex(index);
        }
        return autoIndexedCollection;
    }


    public static <O> IndexedCollection<O> autoPopulateIndexes(IndexedCollection<O> autoIndexedCollection, Class<O> pojoClass) {
        var attributes = generateAttributesListForPojo(pojoClass);

        for (Attribute<O, ? extends Comparable> attribute : attributes) {
            // Add a NavigableIndex...
            @SuppressWarnings("unchecked")
            NavigableIndex<? extends Comparable, O> index = NavigableIndex.onAttribute(attribute);
            autoIndexedCollection.addIndex(index);
        }
        return autoIndexedCollection;
    }

    /**
     * Private constructor, not used.
     */
    CQEngineUtils() {
    }

}
