# 📜 Slot Machine App Requirements (v1.0)

## 1. 개요 (Overview)
- **목표**: Extreme Minimalism 디자인의 슬롯머신 앱.
- **핵심 경험**: 사용자가 레버를 당겼을 때의 묵직한 손맛과 시각적 조화. 완전한 B 급 감성 이미지 에셋이나 로띠

## 2. 핵심 기능 (Core Features)
- [ ] **슬롯 릴 (Slot Reels)**: 3개의 독립적인 릴(Reel)로 구성. 각 릴에는 미니멀한 심볼(먹색 텍스트 또는 기호)이 포함됨.
- [ ] **레버 (Lever/Handle)**: 화면 우측 또는 하단에 배치된 레버. 드래그 또는 터치 인터랙션을 통해 슬롯 실행.
- [ ] **결과 로직**: 릴 회전 후 무작위 결과 산출 및 보상(당첨) 판정 로직.
- [ ] **보상형 광고 충전 (Ad-based Refill)**: 잔액 부족 시 광고를 시청하면 100 크래딧 즉시 충전. 실제 현금 결제(In-app Purchase)는 일절 배제.
- [ ] **스코어/잔액 관리**: 간단한 가상 포인트 시스템. 광고 시청 이력 및 현재 잔액 로컬 저장.


## 3. 디자인 원칙 (Design Principles) - by Jenny
- **Background**: #000000 (심연의 블랙, 네온 대비 극대화)
- **Typography**: 금색 그라데이션 및 네온 글로우가 적용된 볼드한 서체.
- **Animation**: 릴 정지 시 화면 흔들림(Screen Shake), 당첨 시 화려한 폭죽 및 코인 분수 효과.
- **Mood**: 라스베가스 카지노의 B급 감성, 시각적 도파민 자극.

## 4. 기술 제약 사항 (Technical Constraints) - by Kero
- **Platform**: Android (Compose Multiplatform 기반)
- **Architecture**: MVI (Model-View-Intent)
- **Animation**: Compottie (Lottie) 또는 Compose Animation API
- **State Management**: StateFlow + Koin DI
