package com.kgovt.datatable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.kgovt.datatable.paging.Direction;
import com.kgovt.models.ApplicationDetailes;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

public final class ApplicationDetailesComparators {

    @EqualsAndHashCode
    @AllArgsConstructor
    @Getter
    static class Key {
        String name;
        Direction dir;
    }

    static Map<Key, Comparator<ApplicationDetailes>> map = new HashMap<>();

    static {
        map.put(new Key("name", Direction.asc), Comparator.comparing(ApplicationDetailes::getName));
        map.put(new Key("name", Direction.desc), Comparator.comparing(ApplicationDetailes::getName)
                                                           .reversed());
        map.put(new Key("email", Direction.asc), Comparator.comparing(ApplicationDetailes::getEmail));
        map.put(new Key("email", Direction.desc), Comparator.comparing(ApplicationDetailes::getEmail)
                                                           .reversed());
        map.put(new Key("mobile", Direction.asc), Comparator.comparing(ApplicationDetailes::getMobile));
        map.put(new Key("mobile", Direction.desc), Comparator.comparing(ApplicationDetailes::getMobile)
                                                           .reversed());
        map.put(new Key("applicantNumber", Direction.asc), Comparator.comparing(ApplicationDetailes::getApplicantNumber));
        map.put(new Key("applicantNumber", Direction.desc), Comparator.comparing(ApplicationDetailes::getApplicantNumber)
                                                           .reversed());
    }

    public static Comparator<ApplicationDetailes> getComparator(String name, Direction dir) {
        return map.get(new Key(name, dir));
    }

    private ApplicationDetailesComparators() {
    }
}
