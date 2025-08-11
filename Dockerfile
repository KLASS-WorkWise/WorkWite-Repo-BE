# ---- Build Stage ----
#  Sử dụng image Gradle với JDK 21 để build ứng dụng. Đặt tên stage là build.
FROM gradle:8.7.0-jdk21 AS build
# tự nó dow 1 cái gradle 8.7.0 và nó kẹp 1 cái jdk 21 nó build chứ no ko dungf cái j trên máy mik hết , nó tự pull và build tại môi trg docker
# Đặt thư mục làm việc trong container là /app.
WORKDIR /app
# Copy toàn bộ mã nguồn từ máy của mình vào thư mục /app vừa tạo trong container., sau này sẽ ko cóp hết đâu chỉ cóp các thư mục cần thiết thôi
COPY . .
# Chạy lệnh Gradle để build file jar của ứng dụng Spring Boot., no demon là ko có sự tương tác tay của người dùng để sau này đóng gói CL/CI thì nó ko cần hỏi yes, no
RUN gradle clean bootJar --no-daemon

# ---- Run Stage ----
# Sử dụng image do  Amazon Corretto build lên sử dụng  JDK 21 để chạy ứng dụng., gói này gọn nhẹ đủ để chạy  vs java spring boot ( sau này đi làm thì dùn của cái khác )
FROM amazoncorretto:21.0.8
# Đặt lại thư mục làm việc là /app cho stage chạy.
WORKDIR /app
# Copy file jar đã build từ stage build sang stage chạy, đổi tên thành app.jar.
COPY --from=build /app/build/libs/*.jar app.jar
# Mở port 8080 để ứng dụng lắng nghe (thường là port của Spring Boot), ra cổng gì cx dc có thể đổi
EXPOSE 8080
# Thiết lập lệnh khởi chạy ứng dụng khi container start., để chạy lệnh dc terminal ko cần run bằng nút màu xanh trên
ENTRYPOINT ["java","-jar","app.jar"]