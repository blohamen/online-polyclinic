package client.models;

import java.io.Serializable;

public class MedicalServiceAction implements Serializable {
    private MedicalDirection medicalDirection;
    private MedicalService medicalService;
    private MedicalServiceDate medicalServiceDate;
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public MedicalServiceAction() {}

    public MedicalDirection getMedicalDirection() {
        return medicalDirection;
    }

    public void setMedicalDirection(MedicalDirection medicalDirection) {
        this.medicalDirection = medicalDirection;
    }

    public MedicalService getMedicalService() {
        return medicalService;
    }

    public void setMedicalService(MedicalService medicalService) {
        this.medicalService = medicalService;
    }

    public MedicalServiceDate getMedicalServiceDate() {
        return medicalServiceDate;
    }

    public void setMedicalServiceDate(MedicalServiceDate medicalServiceDate) {
        this.medicalServiceDate = medicalServiceDate;
    }
}
