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
- **Background**: #000000 (심연의 블랙, 네온 대비 극대화)
- **Typography**: 금색 그라데이션 및 네온 글로우가 적용된 볼드한 서체.
- **Animation**: 릴 정지 시 화면 흔들림(Screen Shake), 당첨 시 화려한 폭죽 및 코인 분수 효과.
- **Mood**: 라스베가스 카지노의 B급 감성, 시각적 도파민 자극.

## 4. 기술 제약 사항 (Technical Constraints) - by Kero
- **Platform**: Android (Compose Multiplatform 기반)
- **Architecture**: MVI (Model-View-Intent)
- **Animation**: Compottie (Lottie) 또는 Compose Animation API
- **State Management**: StateFlow + Koin DI
