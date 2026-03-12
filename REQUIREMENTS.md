# 📜 Slot Machine App Requirements (v1.0)

## 1. 개요 (Overview)
- **목표**: Extreme Minimalism 디자인의 슬롯머신 앱.
- **핵심 경험**: 'The Calm Ink' 디자인 시스템 기반의 정갈한 UI와 부드러운 애니메이션. 사용자가 레버를 당겼을 때의 묵직한 손맛과 시각적 조화.

## 2. 핵심 기능 (Core Features)
- [ ] **슬롯 릴 (Slot Reels)**: 3개의 독립적인 릴(Reel)로 구성. 각 릴에는 미니멀한 심볼(먹색 텍스트 또는 기호)이 포함됨.
- [ ] **레버 (Lever/Handle)**: 화면 우측 또는 하단에 배치된 레버. 드래그 또는 터치 인터랙션을 통해 슬롯 실행.
- [ ] **결과 로직**: 릴 회전 후 무작위 결과 산출 및 보상(당첨) 판정 로직.
- [ ] **보상 피드백**: 결과에 따른 시각적(애니메이션)/청각적/촉각적(진동) 피드백 제공.
- [ ] **스코어/잔액 관리**: 간단한 가상 포인트 시스템 (초기 설정).

## 3. 디자인 원칙 (Design Principles) - by Jenny
- **Background**: #F9F7F2 (한지 미색, 부드러운 질감 강조)
- **Typography**: 먹색 (#2C2C2C, Pretendard 또는 명조체 계열)
- **Animation**: 부드럽고 점진적인 가속/감속 (Ease-in-out), 인위적이지 않은 움직임.
- **Mood**: 정적인 아름다움, 불필요한 장식(반짝이는 효과 등) 배제.

## 4. 기술 제약 사항 (Technical Constraints) - by Kero
- **Platform**: Android (Compose Multiplatform 기반)
- **Architecture**: MVI (Model-View-Intent)
- **Animation**: Compottie (Lottie) 또는 Compose Animation API
- **State Management**: StateFlow + Koin DI
