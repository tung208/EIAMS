package EIAMS.exception;

import org.hibernate.JDBCException;
import org.hibernate.engine.jdbc.spi.SqlExceptionHelper;

import java.sql.SQLException;

public class DuplicateException extends SqlExceptionHelper {
    public DuplicateException(boolean logWarnings) {
        super(logWarnings);
    }

    @Override
    public JDBCException convert(SQLException sqlException, String message, String sql) {
        // Bạn có thể thực hiện xử lý tùy chỉnh ở đây trước khi trả về JDBCException
        // Ví dụ: log lại exception hoặc thêm thông tin vào message

        // Gọi lại phương thức convert mặc định để xử lý exception
        return super.convert(sqlException, message, sql);
    }
}
