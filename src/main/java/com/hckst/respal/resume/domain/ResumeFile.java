package com.hckst.respal.resume.domain;

import com.hckst.respal.converter.TFCode;
import com.hckst.respal.converter.TFCodeConverter;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
public class ResumeFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originName; // 이미지 파일의 본래 이름

    private String storedName; // 이미지 파일이 S3에 저장될때 사용되는 이름

    private String accessUrl; // S3 내부 이미지에 접근할 수 있는 URL

    // ResumeFile 삭제여부
    @Convert(converter = TFCodeConverter.class)
    @Column(columnDefinition = "char")
    private TFCode deleteYn;
    private LocalDateTime deleteTime;
    private LocalDateTime regTime;

    public static ResumeFile create(String originName) {
        ResumeFile resumeFile = new ResumeFile();
        resumeFile.originName = originName;
        resumeFile.storedName = convertToFileName(originName);
        resumeFile.accessUrl = "";
        resumeFile.deleteYn = TFCode.FALSE;
        resumeFile.regTime = LocalDateTime.now();
        return resumeFile;
    }

    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }
    public void deleteResumeFile(){
        this.deleteYn = TFCode.TRUE;
        this.deleteTime = LocalDateTime.now();
    }

    // 이미지 파일의 확장자를 추출하는 메소드
    private static String extractExtension(String originName) {
        int index = originName.lastIndexOf('.');

        return originName.substring(index, originName.length());
    }

    // 이미지 파일의 이름을 저장하기 위한 이름으로 변환하는 메소드
    private static String convertToFileName(String originName) {
        return UUID.randomUUID() + "." + extractExtension(originName);
    }
}
