-- 신촌세끼 시드 데이터
-- 순서: users -> restaurants -> restaurant_checks
-- 비밀번호: 전부 'password123' (bcrypt)

-- 1. 가상 유저 50명 (학교별 10명)
INSERT INTO users (email, password, name, school, created_at, updated_at) VALUES
  ('yonsei1@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei1', 'YONSEI', NOW(), NOW()),
  ('yonsei2@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei2', 'YONSEI', NOW(), NOW()),
  ('yonsei3@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei3', 'YONSEI', NOW(), NOW()),
  ('yonsei4@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei4', 'YONSEI', NOW(), NOW()),
  ('yonsei5@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei5', 'YONSEI', NOW(), NOW()),
  ('yonsei6@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei6', 'YONSEI', NOW(), NOW()),
  ('yonsei7@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei7', 'YONSEI', NOW(), NOW()),
  ('yonsei8@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei8', 'YONSEI', NOW(), NOW()),
  ('yonsei9@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei9', 'YONSEI', NOW(), NOW()),
  ('yonsei10@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'yonsei10', 'YONSEI', NOW(), NOW()),
  ('ewha1@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha1', 'EWHA', NOW(), NOW()),
  ('ewha2@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha2', 'EWHA', NOW(), NOW()),
  ('ewha3@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha3', 'EWHA', NOW(), NOW()),
  ('ewha4@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha4', 'EWHA', NOW(), NOW()),
  ('ewha5@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha5', 'EWHA', NOW(), NOW()),
  ('ewha6@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha6', 'EWHA', NOW(), NOW()),
  ('ewha7@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha7', 'EWHA', NOW(), NOW()),
  ('ewha8@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha8', 'EWHA', NOW(), NOW()),
  ('ewha9@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha9', 'EWHA', NOW(), NOW()),
  ('ewha10@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'ewha10', 'EWHA', NOW(), NOW()),
  ('sogang1@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang1', 'SOGANG', NOW(), NOW()),
  ('sogang2@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang2', 'SOGANG', NOW(), NOW()),
  ('sogang3@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang3', 'SOGANG', NOW(), NOW()),
  ('sogang4@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang4', 'SOGANG', NOW(), NOW()),
  ('sogang5@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang5', 'SOGANG', NOW(), NOW()),
  ('sogang6@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang6', 'SOGANG', NOW(), NOW()),
  ('sogang7@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang7', 'SOGANG', NOW(), NOW()),
  ('sogang8@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang8', 'SOGANG', NOW(), NOW()),
  ('sogang9@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang9', 'SOGANG', NOW(), NOW()),
  ('sogang10@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'sogang10', 'SOGANG', NOW(), NOW()),
  ('hongik1@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik1', 'HONGIK', NOW(), NOW()),
  ('hongik2@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik2', 'HONGIK', NOW(), NOW()),
  ('hongik3@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik3', 'HONGIK', NOW(), NOW()),
  ('hongik4@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik4', 'HONGIK', NOW(), NOW()),
  ('hongik5@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik5', 'HONGIK', NOW(), NOW()),
  ('hongik6@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik6', 'HONGIK', NOW(), NOW()),
  ('hongik7@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik7', 'HONGIK', NOW(), NOW()),
  ('hongik8@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik8', 'HONGIK', NOW(), NOW()),
  ('hongik9@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik9', 'HONGIK', NOW(), NOW()),
  ('hongik10@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'hongik10', 'HONGIK', NOW(), NOW()),
  ('myongji1@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji1', 'MYONGJI', NOW(), NOW()),
  ('myongji2@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji2', 'MYONGJI', NOW(), NOW()),
  ('myongji3@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji3', 'MYONGJI', NOW(), NOW()),
  ('myongji4@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji4', 'MYONGJI', NOW(), NOW()),
  ('myongji5@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji5', 'MYONGJI', NOW(), NOW()),
  ('myongji6@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji6', 'MYONGJI', NOW(), NOW()),
  ('myongji7@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji7', 'MYONGJI', NOW(), NOW()),
  ('myongji8@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji8', 'MYONGJI', NOW(), NOW()),
  ('myongji9@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji9', 'MYONGJI', NOW(), NOW()),
  ('myongji10@test.com', '$2b$10$1twPhVhBWTrQM3IIxXw.tezjKbZJaHx5LHECbvU7W5VieyJvhbuZm', 'myongji10', 'MYONGJI', NOW(), NOW());

-- 2. 맛집 50개 (카카오 로컬 API, 학교 반경 5km 이내로 제한 검색)
INSERT INTO restaurants (kakao_place_id, name, address, latitude, longitude, image_url, created_at, updated_at) VALUES
  ('1480854338', '담산 신촌본점', '서울 서대문구 연세로5다길 5', 37.556219742880444, 126.93502763937772, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/1480854338/seed.jpeg', NOW(), NOW()),
  ('27144649', '꼬숑돈까스', '서울 서대문구 명물1길 2', 37.55790763826086, 126.93729990100826, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-1.jpeg', NOW(), NOW()),
  ('886596199', '청화원', '서울 서대문구 명물길 10', 37.55741581161588, 126.93752213869575, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/chinese-1.jpeg', NOW(), NOW()),
  ('12421976', '아마 신촌본점', '서울 서대문구 연세로7길 24', 37.557793119842, 126.935457477219, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/chinese-1.jpeg', NOW(), NOW()),
  ('1497585035', '이석덕생면파스타 신촌점', '서울 서대문구 명물길 16', 37.5575979420742, 126.937766448072, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-1.jpeg', NOW(), NOW()),
  ('19243601', '유닭스토리 닭한마리 신촌점', '서울 서대문구 연세로4길 7', 37.557078757253215, 126.93736850101, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('940447278', '카츠업', '서울 서대문구 연세로5길 32', 37.556365896304655, 126.93537722239647, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-1.jpeg', NOW(), NOW()),
  ('2114260452', '소바연구소', '서울 서대문구 명물길 50-9', 37.5587710167753, 126.939454093697, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-3.jpeg', NOW(), NOW()),
  ('11634686', '야바이', '서울 서대문구 연세로7안길 37', 37.5580701585058, 126.934606144916, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-3.jpeg', NOW(), NOW()),
  ('265774533', '송준옥', '서울 서대문구 연세로7안길 34-4', 37.5582740317495, 126.935055281878, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('1118464411', '탁사발 두부공방 이대역점', '서울 서대문구 신촌로 171', 37.5569555213629, 126.945006827041, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/1118464411/seed.jpeg', NOW(), NOW()),
  ('2052782184', '아민 이화', '서울 서대문구 이화여대길 52-31', 37.5594592641306, 126.944732224555, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-2.jpeg', NOW(), NOW()),
  ('1136055554', '반타이 쏘이54', '서울 서대문구 이화여대5길 7-1', 37.55824862372, 126.94537596741064, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/chinese-3.jpeg', NOW(), NOW()),
  ('8126006', '하노이의아침 신촌점', '서울 서대문구 연대동문길 45', 37.564421804712, 126.94441616271801, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/chinese-2.jpeg', NOW(), NOW()),
  ('1960082065', '스시도쿠 현대백화점신촌점', '서울 서대문구 신촌로 83', 37.5559128351594, 126.935634518718, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-1.jpeg', NOW(), NOW()),
  ('18602922', '종금양꼬치', '서울 마포구 대흥로30길 21', 37.556447467835, 126.947219768885, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/chinese-2.jpeg', NOW(), NOW()),
  ('1288074385', '라이프롱드림코지라운지', '서울 서대문구 연세로7길 21', 37.5575661675146, 126.935638755443, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-2.jpeg', NOW(), NOW()),
  ('156463095', '우사골순대국밥', '서울 서대문구 이화여대3길 12', 37.5580655911779, 126.945095421306, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('1770798831', '신촌설렁탕 본점', '서울 서대문구 연세로 24', 37.55771653788253, 126.93713255950883, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('269586816', '모미지식당', '서울 서대문구 이화여대7길 24', 37.55883015675004, 126.94428223894934, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-3.jpeg', NOW(), NOW()),
  ('12502450', '신촌수제비', '서울 서대문구 신촌로 87-8', 37.555901521536555, 126.93637129136783, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/12502450/seed.jpeg', NOW(), NOW()),
  ('26874707', '돈불1971 신촌직영점', '서울 서대문구 연세로5다길 36', 37.5569343697136, 126.933651935508, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-3.jpeg', NOW(), NOW()),
  ('1656269956', '타이완소야진진 신촌본점', '서울 서대문구 연세로7길 27', 37.5575443183092, 126.935226811733, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/chinese-2.jpeg', NOW(), NOW()),
  ('1449183784', '우동카덴 연희점', '서울 서대문구 연희로 173', 37.57240343339889, 126.93502377053203, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-1.jpeg', NOW(), NOW()),
  ('15455029', '청석골감자탕순대국', '서울 마포구 백범로 13', 37.55336488903387, 126.93745082826166, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('864351339', '서강상회', '서울 마포구 독막로22길 17', 37.54699627917329, 126.92886726346002, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-3.jpeg', NOW(), NOW()),
  ('1675683067', '열대야맥주', '서울 서대문구 연세로4길 26', 37.5574234237237, 126.938290599623, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-1.jpeg', NOW(), NOW()),
  ('19400255', '설레임삼겹살1989', '서울 서대문구 연세로5가길 31', 37.5572017702554, 126.934919269541, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-3.jpeg', NOW(), NOW()),
  ('12999212', '신촌돈부리모노 신촌본점', '서울 서대문구 연세로4길 46', 37.5579896172908, 126.938997490118, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-3.jpeg', NOW(), NOW()),
  ('512210695', '스시이안앤 신촌점', '서울 서대문구 신촌로 109', 37.555772646139665, 126.93800336762096, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-3.jpeg', NOW(), NOW()),
  ('1916682638', '오레노라멘 합정본점', '서울 마포구 독막로8길 16', 37.54725761707835, 126.91783721734953, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/1916682638/seed.jpeg', NOW(), NOW()),
  ('27315733', '경주식당', '서울 마포구 와우산로13길 49-7', 37.548424865676, 126.921148208727, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-3.jpeg', NOW(), NOW()),
  ('25754529', '쭌곱창', '서울 마포구 와우산로29마길 6', 37.5558418675072, 126.926722132427, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-3.jpeg', NOW(), NOW()),
  ('23734945', '지로우라멘', '서울 마포구 와우산로29가길 79', 37.5536965208231, 126.925171531217, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-2.jpeg', NOW(), NOW()),
  ('11230411', '퐁포네뜨 홍대점', '서울 마포구 와우산로25길 11', 37.5538915560486, 126.925836781582, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-1.jpeg', NOW(), NOW()),
  ('680686153', '코코리코', '서울 마포구 동교로39길 4', 37.56230576404752, 126.92409899464018, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-2.jpeg', NOW(), NOW()),
  ('268235810', '츠케루', '서울 마포구 와우산로23길 9', 37.5533401874318, 126.924481549594, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-1.jpeg', NOW(), NOW()),
  ('21129871', '고토히라우동', '서울 마포구 와우산로29길 14-8', 37.5550612967006, 126.929171955098, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-2.jpeg', NOW(), NOW()),
  ('15056312', '누구나홀딱반한닭 홍대점', '서울 마포구 어울마당로 129', 37.55600243766151, 126.92417329280818, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('400281849', '우와 홍대본점', '서울 마포구 와우산로21길 21-16', 37.5516835548182, 126.922172337916, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/japanese-3.jpeg', NOW(), NOW()),
  ('26409461', '가타쯔무리', '서울 서대문구 명지대길 72', 37.5832228506648, 126.923466400466, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/26409461/seed.jpeg', NOW(), NOW()),
  ('1536410108', '순이네고릴라떡볶이 본점', '서울 서대문구 거북골로 15-2', 37.580495976590896, 126.92555227861433, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-3.jpeg', NOW(), NOW()),
  ('26068027', '영이호프', '서울 서대문구 거북골로 19-1', 37.5802074297777, 126.925190291359, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-2.jpeg', NOW(), NOW()),
  ('21328592', '엄마손떡볶이 본점', '서울 서대문구 증가로10길 37', 37.5789417794517, 126.924163620995, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('296107551', '명지대박곱창', '서울 서대문구 명지대길 65', 37.582820890372645, 126.92328566834495, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW()),
  ('85479757', '나이트앤포그', '서울 서대문구 증가로 217', 37.5811121089763, 126.914683296997, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-1.jpeg', NOW(), NOW()),
  ('17146711', '주인백파스타 명지대점', '서울 서대문구 증가로10길 57', 37.579519793033526, 126.9249090870615, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-2.jpeg', NOW(), NOW()),
  ('21445025', '락희안 가좌본점', '서울 서대문구 가재울로4길 53', 37.5760982469474, 126.924171032993, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/chinese-1.jpeg', NOW(), NOW()),
  ('1259458479', '리코부리또 명지대점', '서울 서대문구 명지대1길 6', 37.5812918044922, 126.924528045688, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/western-2.jpeg', NOW(), NOW()),
  ('2143790598', '삼정식당', '서울 서대문구 거북골로 25-8', 37.5796378353807, 126.924928214202, 'https://team2-backend-images.s3.ap-northeast-2.amazonaws.com/restaurants/placeholders/korean-1.jpeg', NOW(), NOW());

-- 3. 완료: 학교별 유저 10명이 그 학교 맛집 10개 전부에 완료 처리
--    (맛집당 완료 정확히 10개 = MIN_TOTAL_CHECK_COUNT 충족,
--     전부 같은 학교 유저라 학교별 랭킹 1위 조건도 자동 충족)

-- YONSEI
INSERT INTO restaurant_checks (user_id, restaurant_id, created_at)
SELECT u.id, r.id, NOW()
FROM users u
JOIN restaurants r ON r.kakao_place_id IN (
  '1480854338',
  '27144649',
  '886596199',
  '12421976',
  '1497585035',
  '19243601',
  '940447278',
  '2114260452',
  '11634686',
  '265774533'
)
WHERE u.email LIKE 'yonsei%@test.com';

-- EWHA
INSERT INTO restaurant_checks (user_id, restaurant_id, created_at)
SELECT u.id, r.id, NOW()
FROM users u
JOIN restaurants r ON r.kakao_place_id IN (
  '1118464411',
  '2052782184',
  '1136055554',
  '8126006',
  '1960082065',
  '18602922',
  '1288074385',
  '156463095',
  '1770798831',
  '269586816'
)
WHERE u.email LIKE 'ewha%@test.com';

-- SOGANG
INSERT INTO restaurant_checks (user_id, restaurant_id, created_at)
SELECT u.id, r.id, NOW()
FROM users u
JOIN restaurants r ON r.kakao_place_id IN (
  '12502450',
  '26874707',
  '1656269956',
  '1449183784',
  '15455029',
  '864351339',
  '1675683067',
  '19400255',
  '12999212',
  '512210695'
)
WHERE u.email LIKE 'sogang%@test.com';

-- HONGIK
INSERT INTO restaurant_checks (user_id, restaurant_id, created_at)
SELECT u.id, r.id, NOW()
FROM users u
JOIN restaurants r ON r.kakao_place_id IN (
  '1916682638',
  '27315733',
  '25754529',
  '23734945',
  '11230411',
  '680686153',
  '268235810',
  '21129871',
  '15056312',
  '400281849'
)
WHERE u.email LIKE 'hongik%@test.com';

-- MYONGJI
INSERT INTO restaurant_checks (user_id, restaurant_id, created_at)
SELECT u.id, r.id, NOW()
FROM users u
JOIN restaurants r ON r.kakao_place_id IN (
  '26409461',
  '1536410108',
  '26068027',
  '21328592',
  '296107551',
  '85479757',
  '17146711',
  '21445025',
  '1259458479',
  '2143790598'
)
WHERE u.email LIKE 'myongji%@test.com';
