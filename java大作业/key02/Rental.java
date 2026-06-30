package labMAX.key02;

import java.sql.Timestamp;

public class Rental {
    private int id;
    private int powerId;
    private String userName;
    private Timestamp startTime;
    private Timestamp endTime;
    private double cost;

    public Rental() {
    }

    public Rental(int id, int powerId, String userName, Timestamp startTime, Timestamp endTime, double cost) {
        this.id = id;
        this.powerId = powerId;
        this.userName = userName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPowerId() {
        return powerId;
    }

    public void setPowerId(int powerId) {
        this.powerId = powerId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    @Override
    public String toString() {
        return "Rental{" +
                "id=" + id +
                ", powerId=" + powerId +
                ", userName='" + userName + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", cost=" + cost +
                '}';
    }
}

