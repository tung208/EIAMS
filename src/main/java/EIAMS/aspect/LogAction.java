package EIAMS.aspect;


import EIAMS.entities.Account;
import EIAMS.entities.ActionLog;
import EIAMS.entities.Semester;
import EIAMS.repositories.ActionLogRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Aspect
@Configuration
public class LogAction {
    @Autowired
    ActionLogRepository actionLogRepository;
    @AfterReturning(
            pointcut = "execution(* EIAMS.services.SemesterService.create(..))",
            returning = "result"
    )
    public void actionCreateSemester(JoinPoint joinPoint,Object result){
        // Lấy thông tin lưu vào log
        String className = getClassName(joinPoint.getTarget().getClass().getName());
        String methodName = joinPoint.getSignature().getName();
        String username = getUser();
        Date currentDate = getCurrentDate();

        Semester semester = (Semester) result;
        String name = "";
        if (!username.equals("")){
            name = username.substring(0,username.indexOf('@'));
        }
        ActionLog actionLog = ActionLog.builder()
                .semesterId(semester.getId())
                .userName(username)
                .logTable(className)
                .logAction("Create "+className)
                .logContent(name + " " + methodName + " : " + result)
                .since(currentDate)
                .build();
        actionLogRepository.save(actionLog);
    }

    @AfterReturning(
            pointcut = "execution(* EIAMS.services.SemesterService.update(..))",
            returning = "result"
    )
    public void updateSemester(JoinPoint joinPoint,Object result){
        // Lấy thông tin lưu vào log
        String className = getClassName(joinPoint.getTarget().getClass().getName());
        String methodName = joinPoint.getSignature().getName();
        String username = getUser();
        Date currentDate = getCurrentDate();

        Object[] args = joinPoint.getArgs();
        Semester semester = (Semester) result;

        String name = "";
        if (!username.equals("")){
            name = username.substring(0,username.indexOf('@'));
        }
        ActionLog actionLog = ActionLog.builder()
                .semesterId(semester.getId())
                .userName(username)
                .logTable(className)
                .logAction("Update " + className)
                .logContent(name + " " + methodName + " from " + result + " to " + args[1])
                .since(currentDate)
                .build();
        actionLogRepository.save(actionLog);
    }

    @AfterReturning(
    pointcut = "execution(* EIAMS.services.StudentService.create(..)) || "
            + "execution(* EIAMS.services.StudentService.createStudentSubject(..)) || "
            + "execution(* EIAMS.services.RoomService.create(..)) || "
            + "execution(* EIAMS.services.SubjectService.create(..)) || "
            + "execution(* EIAMS.services.PlanExamService.create(..)) || "
            + "execution(* EIAMS.services.LecturerService.create(..))",
            returning = "result"
    )
    public void actionCreateOthers(JoinPoint joinPoint,Object result) {
        Object[] args = joinPoint.getArgs();
        // Lấy thông tin lưu vào log
        String className = getClassName(joinPoint.getTarget().getClass().getName());
        String methodName = joinPoint.getSignature().getName();
        if (methodName.equals("createStudentSubject")){
            className = "Student Subject";
        }
        String username = getUser();
        Date currentDate = getCurrentDate();
        int semesterId = getSemesterId(args[0].toString());
        String name = "";
        if (!username.equals("")){
            name = username.substring(0,username.indexOf('@'));
        }
        ActionLog actionLog = ActionLog.builder()
                .semesterId(semesterId)
                .userName(username)
                .logTable(className)
                .logAction("Create "+className)
                .logContent(name + " " + methodName + " : " + result)
                .since(currentDate)
                .build();
        actionLogRepository.save(actionLog);
    }

    @AfterReturning(
            pointcut = "execution(* EIAMS.services.StudentService.update(..)) || "
                    + "execution(* EIAMS.services.StudentService.updateStudentSubject(..)) || "
                    + "execution(* EIAMS.services.RoomService.update(..)) || "
                    + "execution(* EIAMS.services.SubjectService.update(..)) || "
                    + "execution(* EIAMS.services.PlanExamService.update(..)) || "
                    + "execution(* EIAMS.services.LecturerService.update(..))",
            returning = "result"
    )
    public void actionUpdateOthers(JoinPoint joinPoint,Object result) {
        Object[] args = joinPoint.getArgs();
        // Lấy thông tin lưu vào log
        String className = getClassName(joinPoint.getTarget().getClass().getName());
        String methodName = joinPoint.getSignature().getName();
        if (methodName.equals("updateStudentSubject")){
            className = "Student Subject";
        }
        String username = getUser();
        Date currentDate = getCurrentDate();
        int semesterId = getSemesterId(args[1].toString());
        String name = "";
        if (!username.equals("")){
            name = username.substring(0,username.indexOf('@'));
        }
        ActionLog actionLog = ActionLog.builder()
                .semesterId(semesterId)
                .userName(username)
                .logTable(className)
                .logAction("Update "+className)
                .logContent(name + " " + methodName + " from " + result + " to " + args[1])
                .since(currentDate)
                .build();
        actionLogRepository.save(actionLog);
    }

    @AfterReturning(
            pointcut = "execution(* EIAMS.services.StudentService.delete(..)) || "
                    + "execution(* EIAMS.services.StudentService.deleteStudentSubject(..)) || "
                    + "execution(* EIAMS.services.RoomService.delete(..)) || "
                    + "execution(* EIAMS.services.SubjectService.delete(..)) || "
                    + "execution(* EIAMS.services.PlanExamService.delete(..)) || "
                    + "execution(* EIAMS.services.LecturerService.delete(..))",
            returning = "result"
    )
    public void actionDeleteOthers(JoinPoint joinPoint,Object result) {
        Object[] args = joinPoint.getArgs();
        // Lấy thông tin lưu vào log
        String className = getClassName(joinPoint.getTarget().getClass().getName());
        String methodName = joinPoint.getSignature().getName();
        if (methodName.equals("updateStudentSubject")){
            className = "Student Subject";
        }
        String username = getUser();
        Date currentDate = getCurrentDate();
        int semesterId = getSemesterId(result.toString());
        String name = "";
        if (!username.equals("")){
            name = username.substring(0,username.indexOf('@'));
        }
        ActionLog actionLog = ActionLog.builder()
                .semesterId(semesterId)
                .userName(username)
                .logTable(className)
                .logAction("Delete "+className)
                .logContent(name + " " + methodName + " : " + result)
                .since(currentDate)
                .build();
        actionLogRepository.save(actionLog);
    }

    @AfterReturning(
            pointcut = "execution(* EIAMS.services.StudentService.uploadStudents(..)) || "
                    + "execution(* EIAMS.services.StudentService.uploadCMND(..)) || "
                    + "execution(* EIAMS.services.StudentService.uploadBlackList(..)) || "
                    + "execution(* EIAMS.services.RoomService.uploadRoom(..)) || "
                    + "execution(* EIAMS.services.PlanExamService.uploadPlanExam(..)) || "
                    + "execution(* EIAMS.services.SubjectService.uploadSubject(..)) || "
                    + "execution(* EIAMS.services.SubjectService.uploadSubjectNoLab(..)) || "
                    + "execution(* EIAMS.services.SubjectService.uploadSubjectDontMix(..)) || "
                    + "execution(* EIAMS.services.LecturerService.uploadLecturer(..))",
            returning = "result"
    )
    public void actionImportOthers(JoinPoint joinPoint,Object result) {
        Object[] args = joinPoint.getArgs();
        // Lấy thông tin lưu vào log
        String className = getClassName(joinPoint.getTarget().getClass().getName());
        String methodName = joinPoint.getSignature().getName();
        String username = getUser();
        Date currentDate = getCurrentDate();
        int semesterId = Integer.parseInt(args[1].toString());
        String name = "";
        if (!username.equals("")){
            name = username.substring(0,username.indexOf('@'));
        }
        ActionLog actionLog = ActionLog.builder()
                .semesterId(semesterId)
                .userName(username)
                .logTable(className)
                .logAction("Upload "+methodName)
                .logContent(name + " " + methodName)
                .since(currentDate)
                .build();
        actionLogRepository.save(actionLog);
    }

    public String getUser(){
        // Lấy thông tin xác thực hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.isAuthenticated() && authentication.getPrincipal() instanceof UserDetails) {
            // Lấy thông tin người dùng từ UserDetails
            Account accountDetails = (Account) authentication.getPrincipal();
            // Lấy username của người dùng
            String username = accountDetails.getUsername();
            String email = accountDetails.getEmail();
            return username;
        } else {
            return "";
        }
    }

    public String getClassName(String className) {
        int lastIndex = className.lastIndexOf('.');
        // Kiểm tra xem có vị trí lastIndex không
        if (lastIndex != -1) {
            // Lấy phần của chuỗi từ vị trí lastIndex đến cuối chuỗi
            String substring = className.substring(lastIndex+1);
            String newStr = substring.replace("Service", "");
            System.out.println("Substring from lastIndex to end: " + newStr); // Output: o world
            return newStr;
        } else {
            System.out.println("Không tìm thấy ký tự 'o' trong chuỗi.");
            return "";
        }
    }

    public Date getCurrentDate(){
        // Lấy ngày và giờ hiện tại
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Chuyển đổi từ LocalDateTime sang Date
        Date currentDate = (Date) java.util.Date.from(currentDateTime.atZone(java.time.ZoneId.systemDefault()).toInstant());

        return currentDate;
    }

    public int getSemesterId(String str) {
        System.out.println(str);
        Pattern pattern = Pattern.compile("semesterId=(\\d+)");
        Matcher matcher = pattern.matcher(str.toString());

        // Nếu tìm thấy match, lấy giá trị semesterId
        if (matcher.find()) {
            String semesterId = matcher.group(1);
            System.out.println("semesterId: " + semesterId);
            return Integer.parseInt(semesterId);
        } else {
            System.out.println("Không tìm thấy semesterId trong chuỗi.");
        }
        return 0;
    }
}
