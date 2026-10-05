package org.example.aptus.dto;

public class SubmitTestResponse {

    private int obtainedMarks;
    private int totalMarks;

    public SubmitTestResponse() {
    }

    public SubmitTestResponse(int obtainedMarks, int totalMarks) {
        this.obtainedMarks = obtainedMarks;
        this.totalMarks = totalMarks;
    }

    public int getObtainedMarks() {
        return obtainedMarks;
    }

    public void setObtainedMarks(int obtainedMarks) {
        this.obtainedMarks = obtainedMarks;
    }

    public int getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(int totalMarks) {
        this.totalMarks = totalMarks;
    }
}
