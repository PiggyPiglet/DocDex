package me.piggypiglet.docdex.db.orm.query.types;

import me.piggypiglet.docdex.db.orm.structure.TableStructure;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// ------------------------------
// Copyright (c) PiggyPiglet 2020
// https://www.piggypiglet.me
// ------------------------------
public final class InsertQuery {
    @NotNull
    public String insert(@NotNull final Map<String, Object> data, @NotNull final TableStructure table) {
        final Map<String, Object> mappedData = new HashMap<>();

        Map<TableStructure, Object> datum = Map.of(table, data);
        do {
            datum.forEach((structure, object) -> mappedData.put(structure.getName(), object));

            final Map<TableStructure, Object> newDatum = new HashMap<>();

            for (final TableStructure parent : datum.keySet()) {
                final String parentName = parent.getName();
                final Map<String, Object> parentMap = (Map<String, Object>) datum.get(parent);

                for (final TableStructure sub : parent.getSubStructures()) {
                    System.out.println(sub.getName() + " - " + parentMap + " - " + sub.getClazz() + " - " + parent.getIdentifier().getName());

                    if (parent.getIdentifier().getName().equals("key")) {
                        final Set<Map<String, Object>> set = new HashSet<>();
                        parentMap.forEach((key, m) -> {
                            final Map<String, Object> dasd = new HashMap<>();
                            dasd.put("key", key);
                            ((Map<String, Object>) m).forEach(dasd::put);
                            set.add(dasd);
                        });

                        newDatum.put(sub, set);
                    } else {
                        newDatum.put(sub, parentMap.get(sub.getName().replace(parentName + '_', "")));
                    }
                }
            }

            datum = newDatum;
        } while (!datum.isEmpty());

        System.out.println(mappedData);

//        final Set<TableStructure> all = Stream.concat(Stream.of(table), Query.getAll(table)).collect(Collectors.toSet());
//        final Map<TableStructure, TableStructure> parents = new HashMap<>();
//
//        all.forEach(structure -> structure.getSubStructures().forEach(sub -> parents.put(sub, structure)));
//
//        final Map<Integer, Map<TableStructure, Map<String, Object>>> levels = new HashMap<>();
//
//        final Map<String, Map<String, Object>> splitData = new HashMap<>();
//
//        all.forEach(structure -> {
//            final Map<String, Object> map = new HashMap<>();
//
//            structure.getColumns().forEach(column -> {
//                final String name = column.getLevel() + ":" + column.getName();
//
//                Map<String, Object> level = data.get(column.getLevel());
//                for (int j = column.getLevel(); j > 0; --j) {
//
//                }
//            });
//
//            splitData.put(structure.getName(), map);
//        });

        return "";
    }
}
