package hello.squadfit.domain.video.service;

public class S3UploadService {
    //        // 2) 파일 검증
//        if (file.isEmpty()) throw new IllegalArgumentException("빈 파일");
//        if (!file.getContentType().startsWith("video/")) throw new IllegalArgumentException("비디오만 허용");
//
//        // 3) 저장 키 생성
//        String key = "videos/%s/%s".formatted(LocalDate.now(), UUID.randomUUID() + "_" + file.getOriginalFilename());
//
//        // 4) 스토리지 업로드 (S3 예시)
//        ObjectMetadata meta = new ObjectMetadata();
//        meta.setContentLength(file.getSize());
//        meta.setContentType(file.getContentType());
//        s3.putObject(new PutObjectRequest(bucket, key, file.getInputStream(), meta));

}
