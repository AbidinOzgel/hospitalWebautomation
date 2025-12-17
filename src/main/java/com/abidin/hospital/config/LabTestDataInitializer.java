package com.abidin.hospital.config;

import com.abidin.hospital.entity.LabTest;
import com.abidin.hospital.repository.LabTestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LabTestDataInitializer implements CommandLineRunner {

    private final LabTestRepository labTestRepository;

    @Override
    public void run(String... args) {
        // Eğer hiç test yoksa, birkaç temel test ekleyelim
        if (labTestRepository.count() == 0) {

            labTestRepository.save(LabTest.builder()
                    .code("HB")
                    .name("Hemoglobin")
                    .unit("g/dL")
                    .refRange("12 - 16")
                    .description("Tam kan sayımı parametresi")
                    .active(true)
                    .build());

            labTestRepository.save(LabTest.builder()
                    .code("GLU")
                    .name("Açlık Kan Şekeri")
                    .unit("mg/dL")
                    .refRange("70 - 100")
                    .description("Açlık plazma glukoz")
                    .active(true)
                    .build());

            labTestRepository.save(LabTest.builder()
                    .code("URE")
                    .name("Üre")
                    .unit("mg/dL")
                    .refRange("10 - 50")
                    .description("Böbrek fonksiyon testi")
                    .active(true)
                    .build());

            labTestRepository.save(LabTest.builder()
                    .code("CRP")
                    .name("CRP")
                    .unit("mg/L")
                    .refRange("< 5")
                    .description("C-reaktif protein, inflamasyon göstergesi")
                    .active(true)
                    .build());

            labTestRepository.save(LabTest.builder()
                    .code("CRE")
                    .name("Kreatinin")
                    .unit("mg/dL")
                    .refRange("0.6 - 1.3")
                    .description("Böbrek fonksiyon testi")
                    .active(true)
                    .build());

            System.out.println("=== DEFAULT LAB TESTLER EKLENDİ ===");
        }
    }
}
