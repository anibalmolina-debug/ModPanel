# Adapter authoring guide

1. Implement `ConfigAdapter`.
2. Declare supported mod IDs, source types, and version ranges.
3. Prefer public APIs over reflection.
4. Fail closed on unknown schema/version.
5. Preserve unrelated/unknown fields.
6. Report restart requirements honestly.
7. Register adapter in `ModPanelClient` with appropriate priority.
