package io.yody.yosurvey.survey.service.helpers;

import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NestedHelper {
    public static <CHILD> List<CHILD> getChilds(Supplier<List<CHILD>> getChildFunction) {
        List<CHILD> childs = getChildFunction.get();
        return childs;
    }

    public static <CHILD1, CHILD2> List<CHILD2> getNestedChilds(
        Supplier<List<CHILD1>> getChild1Function, Function<CHILD1, List<CHILD2>> getChild2Function) {

        List<CHILD1> child1s = getChilds(getChild1Function);
        List<CHILD2> child2s = child1s.stream().flatMap(child1 -> {
            List<CHILD2> child2List = getChild2Function.apply(child1);
            return ObjectUtils.isEmpty(child2List) ? Stream.empty() : child2List.stream();
        }).collect(Collectors.toList());
        return child2s;
    }

    public static <CHILD1, CHILD2, CHILD3> List<CHILD3> getNestedChilds2(
        Supplier<List<CHILD1>> getChild1Function,
        Function<CHILD1, List<CHILD2>> getChild2Function,
        Function<CHILD2, List<CHILD3>> getChild3Function)
    {
        List<CHILD1> child1s = getChilds(getChild1Function);
        List<CHILD2> child2s = child1s.stream().flatMap(child1 -> {
            List<CHILD2> child2List = getChild2Function.apply(child1);
            return ObjectUtils.isEmpty(child2List) ? Stream.empty() : child2List.stream();
        }).collect(Collectors.toList());
        List<CHILD3> child3s = child2s.stream().flatMap(child2 -> {
            List<CHILD3> child3List = getChild3Function.apply(child2);
            return ObjectUtils.isEmpty(child3List) ? Stream.empty() : child3List.stream();
        }).collect(Collectors.toList());
        return child3s;
    }
}
