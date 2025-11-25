-- 더미데이터 초기화
DELETE
FROM record_symptom;
DELETE
FROM record_treatment;
DELETE
FROM prescription_medicine;
DELETE
FROM medicine_ingredient;
DELETE
FROM prescription;
DELETE
FROM medical_record;
DELETE
FROM symptom;
DELETE
FROM medicine;
DELETE
FROM patient;
DELETE
FROM doctor;
DELETE
FROM pharmacist;
DELETE
FROM member;
DELETE
FROM department;
DELETE
FROM pharmacy;
DELETE
FROM hospital;
DELETE
FROM ingredient;
DELETE
FROM treatment;

-- 병원 및 진료과
INSERT INTO hospital (hid, hname, address, phone)
VALUES (1, '한빛대학교병원', '서울시 마포구 백범로 23', '02-100-1000'),
       (2, '푸른숲소아병원', '경기도 성남시 분당구 새롬로 77', '031-200-2000'),
       (3, '새롬메디컬센터', '부산시 해운대구 센텀중앙로 55', '051-300-3000');

INSERT INTO department (dept_id, dname, hid)
VALUES (1, '호흡기내과', 1),
       (2, '소아청소년과', 2),
       (3, '정형외과', 1),
       (4, '심장내과', 3),
       (5, '소화기내과', 1);

-- 약국
INSERT INTO pharmacy (pharm_id, name, address, phone, hid)
VALUES (1, '다온온누리약국', '서울시 용산구 청파로 61', '02-300-3000', 1),
       (2, '늘봄메디칼약국', '경기도 고양시 일산서구 해맞이로 12', '031-400-4000', 2),
       (3, '서울메디케어약국', '서울시 중구 한강대로 30', '02-310-3100', 1),
       (4, '한강메디칼약국', '서울시 중구 통일로 45', '02-360-3600', 1),
       (5, '분당미소약국', '경기도 성남시 분당구 돌마로 52', '031-410-4100', 2),
       (6, '해운대센텀약국', '부산시 해운대구 우동 123', '051-720-7200', 3),
       (7, '센텀메디팜', '부산시 해운대구 APEC로 17', '051-730-7300', 3),
       (8, '광안온누리약국', '부산시 수영구 광안로 201', '051-740-7400', 3);

-- 회원 및 구성원
INSERT INTO member (member_id, username, password, name, role)
VALUES (1, 'doctor.jsh@example.com', '{noop}password', '제승현', 'DOCTOR'),
       (2, 'doctor.lee@example.com', '{noop}password', '이한결', 'DOCTOR'),
       (3, 'doctor.kim@example.com', '{noop}password', '김도훈', 'DOCTOR'),
       (4, 'pharm.choi@example.com', '{noop}password', '최서윤', 'PHARMACIST'),
       (5, 'pharm.han@example.com', '{noop}password', '한지우', 'PHARMACIST'),
       (6, 'doctor.park@example.com', '{noop}password', '박서준', 'DOCTOR'),
       (7, 'pharm.kang@example.com', '{noop}password', '강민아', 'PHARMACIST');

INSERT INTO doctor (did, member_id, hid, dept_id)
VALUES (1, 1, 1, 1),
       (2, 2, 2, 2),
       (3, 3, 1, 3),
       (4, 6, 3, 4);

INSERT INTO pharmacist (phid, member_id, pharm_id)
VALUES (1, 4, 1),
       (2, 5, 2),
       (3, 7, 4);

-- 환자
INSERT INTO patient (pid, name, ssn, address, phone, history)
VALUES (1, '김하늘', '980101-2345678', '서울시 성북구 정릉로 11', '010-1111-2222', '천식으로 입원 이력 있음'),
       (2, '박지민', '010305-3123456', '경기도 수원시 영통구 반달로 57', '010-3333-4444', '알레르기성 비염 진단'),
       (3, '최은서', '050918-4234567', '서울시 송파구 한가람로 29', '010-5555-6666', '만성 두통 관찰 중'),
       (4, '이준영', '921214-1456123', '부산시 해운대구 달맞이길 87', '010-7777-8888', '무릎 수술 후 추적 관찰 중');

-- 증상
INSERT INTO symptom (sid, sname, body_part)
VALUES (1, '기침', '호흡기'),
       (2, '가슴답답함', '가슴'),
       (3, '무릎통증', '무릎'),
       (4, '발열', '전신'),
       (5, '흉통', '가슴'),
       (6, '복통', '복부');

-- 의무기록
INSERT INTO medical_record (rid, visit_date, diagnosis, pid, did)
VALUES (1, '2024-06-01', '계절성 천식 악화로 흡입제 증량 권고', 1, 1),
       (2, '2024-06-15', '상기도 감염 의심, 대증 치료 진행', 2, 2),
       (3, '2024-07-03', '긴장성 두통, 생활습관 교정 및 약물 처방', 3, 1),
       (4, '2024-07-12', '좌측 무릎 연골 손상 재활 경과 확인', 4, 3),
       (5, '2024-08-05', '천식 추적 검사 및 폐기능 평가', 1, 1),
       (6, '2024-08-12', '심근 허혈 추적 검사', 1, 4),
       (7, '2024-08-20', '알레르기 비염 장기 관리', 2, 2),
       (8, '2024-08-28', '긴장성 두통 추적 및 수면 관리 지도', 3, 1),
       (9, '2024-09-02', '좌측 무릎 관절 기능 재평가', 4, 3);

INSERT INTO record_symptom (rid, sid)
VALUES (1, 1),
       (1, 2),
       (2, 1),
       (2, 4),
       (3, 4),
       (4, 3),
       (5, 1),
       (5, 2),
       (6, 5),
       (7, 1),
       (8, 4),
       (9, 3);

-- 의약품
INSERT INTO medicine (mid, mname, manufacturer, efficacy)
VALUES (1, '바이러스퀸정', '한빛제약', '호흡기 염증 완화 및 가래 배출을 돕는 복합제'),
       (2, '콤포르시럽', '서울제약', '소아 발열 및 통증 완화를 위한 시럽제'),
       (3, '무브플렉스캡슐', '코리아메디', '관절 통증 완화와 염증 감소에 도움'),
       (4, '심케어정', '메디칼팜', '협심증 및 심장 기능 보조'),
       (5, '가스프리정', '한빛제약', '위산 과다와 소화불량 완화');

-- 처방 및 조제 상태
INSERT INTO prescription (pres_id, issue_date, status, rid, pharm_id)
VALUES (1, '2024-06-01', '접수', 1, 1),
       (2, '2024-06-15', '조제 중', 2, 1),
       (3, '2024-07-03', '조제 완료', 3, 2),
       (4, '2024-08-05', '접수', 5, 4),
       (5, '2024-08-12', '접수', 6, 6),
       (6, '2024-08-20', '조제 중', 7, 2),
       (7, '2024-08-28', '접수', 8, 3),
       (8, '2024-09-02', '조제 중', 9, 1);

INSERT INTO prescription_medicine (pres_id, mid, dosage, frequency, days)
VALUES (1, 1, '1정', '하루 2회', 7),
       (2, 2, '10ml', '하루 3회', 5),
       (3, 3, '1캡슐', '하루 1회', 14),
       (3, 1, '1정', '하루 1회', 14),
       (4, 1, '1정', '하루 2회', 5),
       (5, 4, '1정', '하루 1회', 7),
       (6, 2, '5ml', '하루 2회', 10),
       (7, 3, '1캡슐', '하루 1회', 7),
       (8, 3, '1캡슐', '하루 1회', 14);

-- 기타 마스터 데이터
INSERT INTO ingredient (iid, iname)
VALUES (1, '아세트아미노펜'),
       (2, '이부프로펜'),
       (3, '니코란들'),
       (4, '돔페리돈');

INSERT INTO treatment (tid, tname, description)
VALUES (1, '흡입 스테로이드 교육', '흡입기 사용법과 위생 관리 교육 제공'),
       (2, '무릎 근력 강화 운동', '재활 운동 루틴과 일일 체크리스트 안내'),
       (3, '심장 재활 지도', '유산소 운동 및 복약 지도'),
       (4, '위장 관리 식단 상담', '소화기 부담을 줄이는 식단 안내');

INSERT INTO medicine_ingredient (mid, iid)
VALUES (1, 1),
       (2, 1),
       (3, 2),
       (4, 3),
       (5, 4);

INSERT INTO record_treatment (rid, tid)
VALUES (1, 1),
       (4, 2),
       (5, 1),
       (6, 3),
       (9, 2);
