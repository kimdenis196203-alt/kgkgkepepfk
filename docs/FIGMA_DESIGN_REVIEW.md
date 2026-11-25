# Figma design review — Личный кабинет успеваемости студента

Source: [Figma Make prototype](https://www.figma.com/make/xpzkWsTeGIuVou2Krnnbte/%D0%9B%D0%B8%D1%87%D0%BD%D1%8B%D0%B9-%D0%BA%D0%B0%D0%B1%D0%B8%D0%BD%D0%B5%D1%82-%D1%83%D1%81%D0%BF%D0%B5%D0%B2%D0%B0%D1%82%D0%B5%D0%BB%D1%8C%D0%BD%D0%BE%D1%81%D1%82%D0%B8-%D1%81%D1%82%D1%83%D0%B4%D0%B5%D0%BD%D1%82%D0%B0?t=qEuvOAbKSsKG4PyI-0)

## Reviewed screen

The supplied Figma Make link exposes a mobile student dashboard / home screen. The visible design includes:
- Student greeting and profile identity at the top, with a notifications control.
- Semester and academic-year context.
- A prominent dark card with average grade (4.72), change (+0.18), and cohort rating (94%).
- A compact summary of completed assignments and upcoming deadlines.
- A discipline list with teacher names, recent grade chips, and average grade.
- A progress section with week / month / semester tabs and a line chart.
- A next-deadline row.
- A fixed bottom navigation with Home, Grades, Analytics, and Profile.

## Visual direction

- Mobile-first, single-column layout with generous horizontal margins and rounded cards.
- Warm off-white page background; near-black / deep-green primary surfaces and text.
- Bright lime-green accent for positive performance and selected states; muted blue for secondary indicators.
- White cards with subtle borders, restrained shadows, and rounded corners.
- Clear typographic hierarchy: small uppercase section captions, bold section headings, oversized average-grade metric.
- Bottom navigation remains visually distinct from the scrolling dashboard content.

## Implementation checklist

1. Build the dashboard as a vertically scrolling screen and keep bottom navigation anchored.
2. Implement reusable components for the performance summary, task/deadline counters, discipline rows, progress chart, and deadline callout.
3. Use a consistent spacing, radius, typography, and color system rather than styling each card independently.
4. Populate the screen from repository/database data; do not hardcode the sample student's identity or grades in production.
5. Add navigation destinations for Grades, Analytics, and Profile; the supplied screen alone does not define those destination layouts.
6. Verify scrolling and bottom-navigation overlap on small Android devices and larger font scales.

## Scope and limitations

This review records only what is visible in the supplied dashboard screenshot/prototype view. It does not assume that the other navigation destinations or detailed interaction states are fully specified. The numeric values and student/teacher names shown are treated as design sample content, not production data.

The screenshot itself is not stored in this repository yet; this document links to the source prototype and records the observed design requirements.
