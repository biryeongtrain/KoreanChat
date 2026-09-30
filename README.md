# KoreanChat

패브릭용 서버사이드 한글 채팅 모드입니다.
서버에만 설치해도 작동합니다
Minecraft 26.3 / Java 25 / Fabric Loader 0.19.5 이상

서버에 Fabric API와 Styled Chat 2.14.0+26.3을 함께 설치해야 합니다.
Player Data API는 KoreanChat JAR에 포함되며, Placeholder API는 Styled Chat에 포함됩니다.

`/krc on` / `/krc off`로 한글채팅을 켜고 끕니다.

Styled Chat이 서식을 파싱한 뒤 텍스트 노드만 한글로 변환합니다.
색상, 그라데이션, URL 클릭, 호버 텍스트와 명령어 데이터를 유지하며,
큰따옴표 안의 영어와 한글·영어가 섞인 메시지는 기존 규칙대로 유지합니다.
변환은 플레이어가 작성한 일반 채팅과 Styled Chat이 처리하는 귓속말·팀 채팅·메시지 명령에 적용됩니다.

Java 25에서 `./gradlew build --console=plain --no-daemon`으로 빌드합니다.
`./gradlew runRegression --console=plain --no-daemon`은 `build/regression-server`의
격리된 로컬 26.3 서버에서 서식·링크·한글 변환·토글 회귀 검사를 실행하고 서버를 종료합니다.
검사는 서버의 서식 데이터를 확인하며, 실제 클라이언트의 표시나 클릭 UI는 별도 확인이 필요합니다.

결과물: `build/libs/KoreanChat-1.6+26.3.jar`

포팅 기준: [Fabric 26.1 포팅 가이드](https://docs.fabricmc.net/26.1.2/develop/porting/),
[26.3 예제 모드](https://github.com/FabricMC/fabric-example-mod/tree/26.3),
[Styled Chat 2.14.0+26.3](https://github.com/Patbox/StyledChat/tree/2.14.0%2B26.3).

이전의 한글채팅 모드가 업뎃이 안되서 만들었습니다
https://github.com/N-Joy-Shadow/FabricChatKoreanConvertor
