# 파일 서비스
파일 저장 및 로드를 주로 담당합니다.  
webflux + mongodb를 기반으로 작동합니다.  
사용자 이미지를 저장할 경우 카프카를 이용해 사용자에게 url을 넘김  
게시판 이미지 저장시 임시 상태로 저장 후 게시글 저장이 완료가 되면 상태를 변경함

## 실행 환경
- openJdk 17
- mongodb
- webFlux
- kafka

# 설정 파일
## application-prod
운영환경에서 사용되는 설정 파일

## application-dev
로컬환경에서 사용되는 설정 파일로 커밋 금지
