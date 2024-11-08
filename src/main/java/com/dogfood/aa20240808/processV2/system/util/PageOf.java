package com.dogfood.aa20240808.processV2.system.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * System built in generic class
 * PageOf
 *
 * @author sys
 */
public class PageOf<T> {
    public Integer number;

    public Boolean last;

    public Integer size;

    public Integer numberOfElements;

    public Integer totalPages;

    public List<T> content = new ArrayList<>();

    public Boolean first;

    public Integer totalElements;

    public Boolean empty;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Integer numberOfElements) {
        this.numberOfElements = numberOfElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Integer getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Integer totalElements) {
        this.totalElements = totalElements;
    }

    public Boolean getEmpty() {
        return empty;
    }

    public void setEmpty(Boolean empty) {
        this.empty = empty;
    }

    public static <T> PageOf of(List<T> content, Long total, Integer page, Integer size) {
        return of(content, null == total ? null : total.intValue(), page, size);
    }

    public static <T> PageOf of(List<T> content, Integer total, Integer page, Integer size) {
        PageOf<T> pageOf = new PageOf();
        pageOf.setContent(content);

        content = ( null == content ? Collections.EMPTY_LIST : content );
        pageOf.setNumberOfElements(content.size());

        if (total != null) {
            pageOf.setEmpty(total == 0);
            pageOf.setTotalElements(total);
        } else {
            total = 0;
        }

        if (null != page && null != size) {
            pageOf.setSize(size);
            pageOf.setNumber(page);
            pageOf.setTotalPages(total / size + (total % size > 0 ? 1 : 0));

            pageOf.setFirst(1 == page);
            pageOf.setLast(page.equals(pageOf.getTotalPages()));
        }
        return pageOf;
    }

    @Override
    public String toString() {
        return (
            "PageOf{" +
            "number=" +
            number +
            ", last=" +
            last +
            ", size=" +
            size +
            ", numberOfElements=" +
            numberOfElements +
            ", totalPages=" +
            totalPages +
            ", content=" +
            content +
            ", first=" +
            first +
            ", totalElements=" +
            totalElements +
            ", empty=" +
            empty +
            '}'
        );
    }
}
