package org.elsveys.service;

import jakarta.persistence.EntityManager;
import org.elsveys.entity.Assignment;
import org.elsveys.repository.AssignmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class AssignmentService {
    private final AssignmentRepository assignmentRepository;
    private final EntityManager entityManager;

    public AssignmentService(AssignmentRepository assignmentRepository, EntityManager entityManager){
        this.assignmentRepository = assignmentRepository;
        this.entityManager = entityManager;
    }

    public Assignment getAssignment(Integer id){
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    public List<Assignment> getUserAssignments(Integer taskId){
        return assignmentRepository.findByTaskIdAndIsDeleted(taskId, false); // Only active
    }

    public List<Assignment> getAssignmentsOfUsers(Integer userId){
        return assignmentRepository.findByUserIdAndIsDeleted(userId, false); // Only active
    }
    @Transactional
    public void deleteAssignmentsByTaskId(Integer taskId) {
        assignmentRepository.softDeleteByTaskId(taskId, LocalDate.now());
    }

    public Assignment createAssignment(Assignment assignment){
        return assignmentRepository.save(assignment);
    }

    public Assignment updateAssignment(Assignment assignment){
        return assignmentRepository.save(assignment);
    }

    public void softDeleteAssignment(Integer id){
        entityManager.createNativeQuery("CALL soft_delete_assignment(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public void restoreAssignment(Integer id){
        entityManager.createNativeQuery("CALL restore_assignment(:id)")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<Assignment> getAllAssignments(){
        return assignmentRepository.findAll();
    }
}
