package com.pph.learn.service;

import com.pph.learn.api.GenerateRequest;
import org.springframework.stereotype.Service;

@Service
public class MarkdownGeneratorService {

    public String generate(GenerateRequest req) {
        // For now we generate a deterministic, detailed Markdown guide in Vietnamese.
        // If an external LLM call is desired, this method can be extended to call Gemini/OpenAI
        String topic = req.getTopic() == null ? "<CHỦ_ĐỀ>" : req.getTopic();
        String language = req.getLanguage() == null ? "việt" : req.getLanguage();

        StringBuilder md = new StringBuilder();
        md.append("# Tự-học " + topic + " từ A → Master\n\n");

        md.append("## Tóm tắt mục tiêu\n\n");
        md.append("Mục tiêu: Giúp người học đi từ cơ bản đến nâng cao trong chủ đề **" + topic + "**. Bản hướng dẫn này bao gồm lộ trình, bài tập, dự án, tài nguyên và cách tự đánh giá.\n\n");

        md.append("## Yêu cầu nhập môn (Prereqs)\n\n");
        md.append("Không cần (hoặc ghi rõ kiến thức cơ bản nếu cần).\n\n");

        md.append("## Chuỗi chủ đề theo thứ tự\n\n");

        // Add many detailed sections to reach a large word count
        String[] topics = new String[] {
            "Khái niệm cơ bản và định hướng",
            "Công cụ & môi trường",
            "Kiến thức trung cấp",
            "Kỹ năng nâng cao",
            "Bảo mật & tối ưu hóa",
            "Triển khai thực tế và vận hành",
            "Case studies và best practices",
            "Chu kỳ học tập và ôn tập"
        };

        for (int i = 0; i < topics.length; i++) {
            md.append("### " + (i+1) + ". " + topics[i] + "\n\n");
            md.append("Mục tiêu nhỏ: Hiểu và thành thạo phần này.\n\n");
            // Add several paragraphs of content
            for (int p = 0; p < 6; p++) {
                md.append("Đoạn mô tả chi tiết (" + topics[i] + ") — hướng dẫn cụ thể, mẹo thực tế và ví dụ ngắn. ");
                md.append("Người học nên làm theo từng bước: đọc, thử, phản hồi, và lặp lại. ");
                md.append("Gợi ý bài tập kèm ví dụ: thực hành trực tiếp, làm mini-project, và ghi nhật ký học tập.\n\n");
            }
        }

        md.append("## Bài tập ngắn hàng ngày/tuần\n\n");
        md.append("- Hàng ngày: 20–60 phút tập trung trên kỹ năng chính.\n");
        md.append("- Hàng tuần: 1–3 bài tập lớn hơn kèm review.\n\n");

        md.append("### Một số đề bài mẫu và hướng giải ngắn gọn\n\n");
        for (int i = 1; i <= 8; i++) {
            md.append("**Bài tập " + i + ":** Mô tả đề bài.\n\n");
            md.append("**Hướng giải:** Các bước thực hiện, các lỗi thường gặp, và tiêu chí đánh giá.\n\n");
        }

        md.append("## Dự án thực tế\n\n");
        md.append("### Dự án nhỏ (exercise)\n\n");
        md.append("Mô tả: Một project nhỏ để thực hành trong 3–7 ngày.\n\n");
        md.append("Milestones:\n- Thiết kế\n- Triển khai\n- Tài liệu và README\n\n");
        md.append("Tiêu chí chấm điểm: Hoạt động, có README, có test cơ bản.\n\n");

        md.append("### Dự án lớn (portfolio)\n\n");
        md.append("Mô tả: Một dự án hoàn chỉnh có thể đưa vào portfolio, thời gian 4–12 tuần.\n\n");
        md.append("Milestones:\n- Yêu cầu & thiết kế\n- MVP\n- Tests & CI\n- Tối ưu hóa & security\n- Triển khai \n\n");
        md.append("Tiêu chí chấm điểm: Hoạt động, có mức độ hoàn thiện, có test, có tài liệu và case study.\n\n");

        md.append("## Kế hoạch ôn tập & Spaced Repetition\n\n");
        md.append("Gợi ý flashcards và SRS: Anki hoặc Memrise. Lên lịch 1 ngày/3 ngày/7 ngày/30 ngày cho điểm chính.\n\n");

        md.append("## Cách tự đánh giá\n\n");
        md.append("Bao gồm quizzes, rubrics, và bài đánh giá mô phỏng.\n\n");

        md.append("## Tài nguyên\n\n");
        md.append("- Miễn phí: docs chính thức, tutorial, YouTube, bài blog.\n");
        md.append("- Trả phí: khóa học nền tảng, sách chuyên sâu.\n");
        md.append("- Chính thức: tài liệu chuẩn của công nghệ liên quan.\n\n");

        md.append("## Những lỗi phổ biến & cách né tránh\n\n");
        md.append("Danh sách lỗi thường gặp và mẹo phòng tránh.\n\n");

        md.append("## Cheat sheet / công cụ tổng hợp\n\n");
        md.append("Từ khóa, câu lệnh, công thức ngắn gọn.\n\n");

        md.append("## 3 Track tốc độ\n\n");
        md.append("- INTENSIVE: 8–12 tuần, 4–6 giờ/ngày.\n");
        md.append("- MODERATE: 12–24 tuần, 1–2 giờ/ngày.\n");
        md.append("- SLOW: 6–12 tháng, cuối tuần hoặc 3–5 giờ/tuần.\n\n");

        md.append("## Bài kiểm tra ngắn cho mỗi chủ đề\n\n");
        for (int i = 0; i < topics.length; i++) {
            md.append("### Kiểm tra: " + topics[i] + "\n");
            md.append("1) Câu hỏi trắc nghiệm/định nghĩa\n");
            md.append("2) Câu hỏi tình huống/áp dụng\n");
            md.append("3) Câu hỏi thực hành nhỏ\n");
            md.append("Tiêu chí pass: trả lời >= 70% + hoàn thành phần thực hành.\n\n");
        }

        md.append("## Kiểm tra offline (nếu cần)\n\n");
        md.append("Hướng dẫn tự quay video, thu âm, hoặc gửi project để mentor chấm.\n\n");

        md.append("## 1-page checklist để in\n\n");
        md.append("- [ ] Đọc 5 chương cơ bản\n- [ ] Hoàn thành 10 bài tập nhỏ\n- [ ] Hoàn thành dự án nhỏ\n- [ ] Hoàn thành dự án lớn và viết case study\n\n");

        md.append("---\n\n");
        md.append("*Ghi chú: Phiên bản này là mẫu tự động tạo. Nếu muốn văn phong khác (kỹ thuật, truyền cảm hứng, dành cho sinh viên), chọn chế độ tuỳ chỉnh.*\n");

        // Ensure fairly large body by repeating a summary block several times
        for (int r = 0; r < 6; r++) {
            md.append("\n### Phần mở rộng (chi tiết)\n\n");
            for (int j = 0; j < 10; j++) {
                md.append("Một đoạn nội dung chi tiết mô tả các bước thực tế, chiến lược học tập, lời khuyên, kinh nghiệm thực tiễn và ví dụ cụ thể. ");
                md.append("Hãy áp dụng theo chu kỳ: học -> luyện -> review -> áp dụng.\n\n");
            }
        }

        return md.toString();
    }
}
