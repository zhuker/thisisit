package com.vg.thisisit;

class Moment {
    int start; //inclusive
    int end; //inclusive

    Moment(int start, int end) {
        this.start = start;
        this.end = end;
    }

    int duration() {
        return end - start + 1;
    }

    @Override
    public String toString() {
        return "Moment{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
