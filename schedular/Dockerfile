FROM ghcr.io/oracle/oraclelinux:9-slim

EXPOSE 8080

COPY target/schedule app

ENTRYPOINT ["/app"]

# Pull and run:
# podman pull ghcr.io/swseighman/actions-demo:latest
# podman run --rm -p 8080:8080 ghcr.io/swseighman/actions-demo:latest